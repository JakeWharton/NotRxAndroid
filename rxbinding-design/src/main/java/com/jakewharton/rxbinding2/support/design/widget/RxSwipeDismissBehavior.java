package com.jakewharton.rxbinding2.support.design.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.design.widget.SwipeDismissBehavior;
import android.view.View;
import io.reactivex.Observable;

/**
 * Static factory methods for creating {@linkplain Observable observables}
 * for {@link SwipeDismissBehavior} on (@link View).
 */
public final class RxSwipeDismissBehavior {
  /**
   * Create an observable which emits the dismiss events from {@code view} on
   * {@link SwipeDismissBehavior}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static Observable<View> dismisses(@NonNull View view) {
    return new SwipeDismissBehaviorObservable(view);
  }

  private RxSwipeDismissBehavior() {
    throw new AssertionError("No instances.");
  }
}
