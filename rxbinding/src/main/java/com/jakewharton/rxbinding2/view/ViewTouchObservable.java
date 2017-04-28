package com.jakewharton.rxbinding2.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Predicate;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

final class ViewTouchObservable extends Observable<MotionEvent> {
  private final View view;
  private final Predicate<? super MotionEvent> handled;

  ViewTouchObservable(View view, Predicate<? super MotionEvent> handled) {
    this.view = view;
    this.handled = handled;
  }

  @Override protected void subscribeActual(Observer<? super MotionEvent> observer) {
    if (!checkMainThread(observer)) {
      return;
    }
    Listener listener = new Listener(view, handled, observer);
    observer.onSubscribe(listener);
    view.setOnTouchListener(listener);
  }

  static final class Listener extends MainThreadDisposable implements OnTouchListener {
    private final View view;
    private final Predicate<? super MotionEvent> handled;
    private final Observer<? super MotionEvent> observer;

    Listener(View view, Predicate<? super MotionEvent> handled,
        Observer<? super MotionEvent> observer) {
      this.view = view;
      this.handled = handled;
      this.observer = observer;
    }

    @Override public boolean onTouch(View v, MotionEvent event) {
      if (!isDisposed()) {
        try {
          observer.onNext(event);
          return handled.test(event);
        } catch (Exception e) {
          observer.onError(e);
          dispose();
        }
      }
      return false;
    }

    @Override protected void onDispose() {
      view.setOnTouchListener(null);
    }
  }
}
