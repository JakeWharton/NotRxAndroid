package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Adapter;
import android.widget.AdapterView;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Functions;
import java.util.concurrent.Callable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain Consumer
 * actions} for {@link AdapterView}.
 */
public final class RxAdapterView {
  /**
   * Create an observable of the selected position of {@code view}. If nothing is selected,
   * {@link AdapterView#INVALID_POSITION} will be emitted.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Note:</em> A value will be emitted immediately on subscribe.
   */
  @CheckResult @NonNull
  public static <T extends Adapter> InitialValueObservable<Integer> itemSelections(
      @NonNull AdapterView<T> view) {
    return new AdapterViewItemSelectionObservable(view);
  }

  /**
   * Create an observable of selection events for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Note:</em> A value will be emitted immediately on subscribe.
   */
  @CheckResult @NonNull
  public static <T extends Adapter> InitialValueObservable<AdapterViewSelectionEvent>
  selectionEvents(@NonNull AdapterView<T> view) {
    return new AdapterViewSelectionObservable(view);
  }

  /**
   * Create an observable of the position of item clicks for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static <T extends Adapter> Observable<Integer> itemClicks(
      @NonNull AdapterView<T> view) {
    return new AdapterViewItemClickObservable(view);
  }

  /**
   * Create an observable of the item click events for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static <T extends Adapter> Observable<AdapterViewItemClickEvent> itemClickEvents(
      @NonNull AdapterView<T> view) {
    return new AdapterViewItemClickEventObservable(view);
  }

  /**
   * Create an observable of the position of item long-clicks for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static <T extends Adapter> Observable<Integer> itemLongClicks(
      @NonNull AdapterView<T> view) {
    return itemLongClicks(view, Functions.CALLABLE_ALWAYS_TRUE);
  }

  /**
   * Create an observable of the position of item long-clicks for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   *
   * @param handled Function invoked each occurrence to determine the return value of the
   * underlying {@link AdapterView.OnItemLongClickListener}.
   */
  @CheckResult @NonNull
  public static <T extends Adapter> Observable<Integer> itemLongClicks(@NonNull AdapterView<T> view,
      @NonNull Callable<Boolean> handled) {
    return new AdapterViewItemLongClickObservable(view, handled);
  }

  /**
   * Create an observable of the item long-click events for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static <T extends Adapter> Observable<AdapterViewItemLongClickEvent> itemLongClickEvents(
      @NonNull AdapterView<T> view) {
    return itemLongClickEvents(view, Functions.PREDICATE_ALWAYS_TRUE);
  }

  /**
   * Create an observable of the item long-click events for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   *
   * @param handled Function invoked with each value to determine the return value of the
   * underlying {@link AdapterView.OnItemLongClickListener}.
   */
  @CheckResult @NonNull
  public static <T extends Adapter> Observable<AdapterViewItemLongClickEvent> itemLongClickEvents(
      @NonNull AdapterView<T> view,
      @NonNull Predicate<? super AdapterViewItemLongClickEvent> handled) {
    return new AdapterViewItemLongClickEventObservable(view, handled);
  }

  /**
   * An action which sets the selected position of {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static <T extends Adapter> Consumer<? super Integer> selection(
      @NonNull final AdapterView<T> view) {
    return new Consumer<Integer>() {
      @Override public void accept(Integer position) {
        view.setSelection(position);
      }
    };
  }

  private RxAdapterView() {
    throw new AssertionError("No instances.");
  }
}
