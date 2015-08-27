package com.jakewharton.rxbinding.view;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import com.jakewharton.rxbinding.RecordingObserver;
import rx.functions.Action1;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_HOVER_ENTER;
import static android.view.MotionEvent.ACTION_HOVER_EXIT;
import static android.view.MotionEvent.ACTION_HOVER_MOVE;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;
import static com.jakewharton.rxbinding.MotionEventUtil.motionEventAtPosition;

@RunWith(AndroidJUnit4.class)
public final class RxViewTest {
  @Rule public final UiThreadTestRule uiThread = new UiThreadTestRule();

  private final Context context = InstrumentationRegistry.getContext();
  private final View view = new View(context);

  @Test @UiThreadTest public void clicks() {
    RecordingObserver<Object> o = new RecordingObserver<>();
    Subscription subscription = RxView.clicks(view).subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    view.performClick();
    assertThat(o.takeNext()).isNotNull();

    view.performClick();
    assertThat(o.takeNext()).isNotNull();

    subscription.unsubscribe();

    view.performClick();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void clickEvents() {
    RecordingObserver<ViewClickEvent> o = new RecordingObserver<>();
    Subscription subscription = RxView.clickEvents(view).subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    view.performClick();
    assertThat(o.takeNext()).isEqualTo(ViewClickEvent.create(view));

    view.performClick();
    assertThat(o.takeNext()).isEqualTo(ViewClickEvent.create(view));

    subscription.unsubscribe();

    view.performClick();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void drags() {
    //RecordingObserver<ViewClickEvent> o = new RecordingObserver<>();
    //Subscription subscription = RxView.clickEvents(view).subscribe(o);
    //o.assertNoMoreEvents(); // No initial value.
    //
    //clock.advance(1, SECONDS);
    //view.performClick();
    //assertThat(o.takeNext()).isEqualTo(ViewClickEvent.create(view, 1000));
    //
    //clock.advance(1, SECONDS);
    //view.performClick();
    //assertThat(o.takeNext()).isEqualTo(ViewClickEvent.create(view, 2000));
    //
    //subscription.unsubscribe();
    //
    //clock.advance(1, SECONDS);
    //view.performClick();
    //o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void focusChanges() {
    // We need a parent which can take focus from our view when it attempts to clear.
    LinearLayout parent = new LinearLayout(context);
    parent.setFocusable(true);
    parent.addView(view);

    view.setFocusable(true);

    RecordingObserver<Boolean> o = new RecordingObserver<>();
    Subscription subscription = RxView.focusChanges(view).subscribe(o);
    assertThat(o.takeNext()).isFalse();

    view.requestFocus();
    assertThat(o.takeNext()).isTrue();

    view.clearFocus();
    assertThat(o.takeNext()).isFalse();

    subscription.unsubscribe();

    view.requestFocus();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void focusChangeEvents() {
    // We need a parent which can take focus from our view when it attempts to clear.
    LinearLayout parent = new LinearLayout(context);
    parent.setFocusable(true);
    parent.addView(view);

    view.setFocusable(true);

    RecordingObserver<ViewFocusChangeEvent> o = new RecordingObserver<>();
    Subscription subscription = RxView.focusChangeEvents(view).subscribe(o);
    assertThat(o.takeNext()).isEqualTo(ViewFocusChangeEvent.create(view, false));

    view.requestFocus();
    assertThat(o.takeNext()).isEqualTo(ViewFocusChangeEvent.create(view, true));

    view.clearFocus();
    assertThat(o.takeNext()).isEqualTo(ViewFocusChangeEvent.create(view, false));

    subscription.unsubscribe();

    view.requestFocus();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void hovers() {
    RecordingObserver<MotionEvent> o = new RecordingObserver<>();
    Subscription subscription = RxView.hovers(view).subscribe(o);
    o.assertNoMoreEvents();

    view.dispatchGenericMotionEvent(motionEventAtPosition(view, ACTION_HOVER_ENTER, 0, 50));
    MotionEvent event1 = o.takeNext();
    assertThat(event1.getAction()).isEqualTo(ACTION_HOVER_ENTER);

    view.dispatchGenericMotionEvent(motionEventAtPosition(view, ACTION_HOVER_MOVE, 1, 50));
    MotionEvent event2 = o.takeNext();
    assertThat(event2.getAction()).isEqualTo(ACTION_HOVER_MOVE);

    subscription.unsubscribe();

    view.dispatchGenericMotionEvent(motionEventAtPosition(view, ACTION_HOVER_EXIT, 1, 50));
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void hoverEvents() {
    RecordingObserver<ViewHoverEvent> o = new RecordingObserver<>();
    Subscription subscription = RxView.hoverEvents(view).subscribe(o);
    o.assertNoMoreEvents();

    view.dispatchGenericMotionEvent(motionEventAtPosition(view, ACTION_HOVER_ENTER, 0, 50));
    ViewHoverEvent event1 = o.takeNext();
    assertThat(event1.view()).isSameAs(view);
    assertThat(event1.motionEvent().getAction()).isEqualTo(ACTION_HOVER_ENTER);

    view.dispatchGenericMotionEvent(motionEventAtPosition(view, ACTION_HOVER_MOVE, 1, 50));
    ViewHoverEvent event2 = o.takeNext();
    assertThat(event2.view()).isSameAs(view);
    assertThat(event2.motionEvent().getAction()).isEqualTo(ACTION_HOVER_MOVE);

    subscription.unsubscribe();

    view.dispatchGenericMotionEvent(motionEventAtPosition(view, ACTION_HOVER_EXIT, 1, 50));
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void longClicks() {
    // We need a parent because long presses delegate to the parent.
    LinearLayout parent = new LinearLayout(context) {
      @Override public boolean showContextMenuForChild(View originalView) {
        return true;
      }
    };
    parent.addView(view);

    RecordingObserver<Object> o = new RecordingObserver<>();
    Subscription subscription = RxView.longClicks(view).subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    view.performLongClick();
    assertThat(o.takeNext()).isNotNull();

    view.performLongClick();
    assertThat(o.takeNext()).isNotNull();

    subscription.unsubscribe();

    view.performLongClick();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void longClickEvents() {
    // We need a parent because long presses delegate to the parent.
    LinearLayout parent = new LinearLayout(context) {
      @Override public boolean showContextMenuForChild(View originalView) {
        return true;
      }
    };
    parent.addView(view);

    RecordingObserver<ViewLongClickEvent> o = new RecordingObserver<>();
    Subscription subscription = RxView.longClickEvents(view).subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    view.performLongClick();
    assertThat(o.takeNext()).isEqualTo(ViewLongClickEvent.create(view));

    view.performLongClick();
    assertThat(o.takeNext()).isEqualTo(ViewLongClickEvent.create(view));

    subscription.unsubscribe();

    view.performLongClick();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void touches() {
    RecordingObserver<MotionEvent> o = new RecordingObserver<>();
    Subscription subscription = RxView.touches(view).subscribe(o);
    o.assertNoMoreEvents();

    view.dispatchTouchEvent(motionEventAtPosition(view, ACTION_DOWN, 0, 50));
    MotionEvent event1 = o.takeNext();
    assertThat(event1.getAction()).isEqualTo(ACTION_DOWN);

    view.dispatchTouchEvent(motionEventAtPosition(view, ACTION_MOVE, 1, 50));
    MotionEvent event2 = o.takeNext();
    assertThat(event2.getAction()).isEqualTo(ACTION_MOVE);

    subscription.unsubscribe();

    view.dispatchTouchEvent(motionEventAtPosition(view, ACTION_UP, 1, 50));
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void touchEvents() {
    RecordingObserver<ViewTouchEvent> o = new RecordingObserver<>();
    Subscription subscription = RxView.touchEvents(view).subscribe(o);
    o.assertNoMoreEvents();

    view.dispatchTouchEvent(motionEventAtPosition(view, ACTION_DOWN, 0, 50));
    ViewTouchEvent event1 = o.takeNext();
    assertThat(event1.view()).isSameAs(view);
    assertThat(event1.motionEvent().getAction()).isEqualTo(ACTION_DOWN);

    view.dispatchTouchEvent(motionEventAtPosition(view, ACTION_MOVE, 1, 50));
    ViewTouchEvent event2 = o.takeNext();
    assertThat(event2.view()).isSameAs(view);
    assertThat(event2.motionEvent().getAction()).isEqualTo(ACTION_MOVE);

    subscription.unsubscribe();

    view.dispatchTouchEvent(motionEventAtPosition(view, ACTION_UP, 1, 50));
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void activated() {
    view.setActivated(true);
    Action1<? super Boolean> action = RxView.activated(view);
    action.call(false);
    assertThat(view.isActivated()).isFalse();
    action.call(true);
    assertThat(view.isActivated()).isTrue();
  }

  @Test @UiThreadTest public void clickable() {
    view.setClickable(true);
    Action1<? super Boolean> action = RxView.clickable(view);
    action.call(false);
    assertThat(view.isClickable()).isFalse();
    action.call(true);
    assertThat(view.isClickable()).isTrue();
  }

  @Test @UiThreadTest public void enabled() {
    view.setEnabled(true);
    Action1<? super Boolean> action = RxView.enabled(view);
    action.call(false);
    assertThat(view.isEnabled()).isFalse();
    action.call(true);
    assertThat(view.isEnabled()).isTrue();
  }

  @Test @UiThreadTest public void pressed() {
    view.setPressed(true);
    Action1<? super Boolean> action = RxView.pressed(view);
    action.call(false);
    assertThat(view.isPressed()).isFalse();
    action.call(true);
    assertThat(view.isPressed()).isTrue();
  }

  @Test @UiThreadTest public void selected() {
    view.setSelected(true);
    Action1<? super Boolean> action = RxView.selected(view);
    action.call(false);
    assertThat(view.isSelected()).isFalse();
    action.call(true);
    assertThat(view.isSelected()).isTrue();
  }

  @Test @UiThreadTest public void visibility() {
    view.setVisibility(View.VISIBLE);
    Action1<? super Boolean> action = RxView.visibility(view);
    action.call(false);
    assertThat(view.getVisibility()).isEqualTo(View.GONE);
    action.call(true);
    assertThat(view.getVisibility()).isEqualTo(View.VISIBLE);
  }

  @Test @UiThreadTest public void visibilityCustomFalse() {
    view.setVisibility(View.VISIBLE);
    Action1<? super Boolean> action = RxView.visibility(view, View.INVISIBLE);
    action.call(false);
    assertThat(view.getVisibility()).isEqualTo(View.INVISIBLE);
    action.call(true);
    assertThat(view.getVisibility()).isEqualTo(View.VISIBLE);
  }

  @Test @UiThreadTest public void setVisibilityCustomFalseToVisibleThrows() {
    try {
      RxView.visibility(view, View.VISIBLE);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Setting visibility to VISIBLE when false would have no effect.");
    }
  }
}
