package com.jakewharton.rxbinding2.support.v4.view;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;
import com.jakewharton.rxbinding2.RecordingObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public final class RxViewPagerTest {
  @Rule public final ActivityTestRule<RxViewPagerTestActivity> activityRule =
      new ActivityTestRule<>(RxViewPagerTestActivity.class);

  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  private ViewPager view;

  @Before public void setUp() {
    RxViewPagerTestActivity activity = activityRule.getActivity();
    view = activity.viewPager;
  }

  @Test public void pageScrollEvents() {
    view.setCurrentItem(0);
    RecordingObserver<ViewPagerPageScrollEvent> o1 = new RecordingObserver<>();
    RxViewPager.pageScrollEvents(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o1);
    o1.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override
      public void run() {
        view.setCurrentItem(1, true);
      }
    });
    instrumentation.waitForIdleSync();
    ViewPagerPageScrollEvent event1 = o1.takeNext();
    assertEquals(0, event1.position());
    assertTrue(event1.positionOffset() > 0f);
    assertTrue(event1.positionOffsetPixels() > 0);

    o1.dispose();
    o1.assertNoMoreEvents();

    RecordingObserver<ViewPagerPageScrollEvent> o2 = new RecordingObserver<>();
    RxViewPager.pageScrollEvents(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o2);
    o2.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override
      public void run() {
        view.setCurrentItem(0, true);
      }
    });
    instrumentation.waitForIdleSync();
    ViewPagerPageScrollEvent event2 = o2.takeNext();
    assertEquals(0, event2.position());
    assertTrue(event2.positionOffset() < 1f);
    assertTrue(event2.positionOffsetPixels() < view.getWidth());

    o2.dispose();
    o2.assertNoMoreEvents();
  }

  @Test public void pageScrollStateChanges() {
    view.setCurrentItem(0);
    RecordingObserver<Integer> o = new RecordingObserver<>();
    RxViewPager.pageScrollStateChanges(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    onView(withId(1)).perform(swipeLeft());
    assertEquals(ViewPager.SCROLL_STATE_DRAGGING, o.takeNext().intValue());
    assertEquals(ViewPager.SCROLL_STATE_SETTLING, o.takeNext().intValue());
    assertEquals(ViewPager.SCROLL_STATE_IDLE, o.takeNext().intValue());
    o.assertNoMoreEvents();

    o.dispose();

    onView(withId(1)).perform(swipeLeft());
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void pageSelections() {
    view.setCurrentItem(0);
    RecordingObserver<Integer> o = new RecordingObserver<>();
    RxViewPager.pageSelections(view).subscribe(o);
    assertEquals(0, o.takeNext().intValue());

    view.setCurrentItem(3);
    assertEquals(3, o.takeNext().intValue());
    view.setCurrentItem(5);
    assertEquals(5, o.takeNext().intValue());

    o.dispose();

    view.setCurrentItem(0);
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void currentItem() throws Exception {
    Consumer<? super Integer> action = RxViewPager.currentItem(view);
    action.accept(3);
    assertEquals(3, view.getCurrentItem());
    action.accept(5);
    assertEquals(5, view.getCurrentItem());
  }

  private static ViewAction swipeLeft() {
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT,
        GeneralLocation.CENTER_LEFT, Press.FINGER);
  }
}
