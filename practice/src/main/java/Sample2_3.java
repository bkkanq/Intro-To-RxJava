import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.observable.ObservableInternalHelper;

public class Sample2_3 {

  public void all_success() {
    // The all method established that every value emitted by an observable meets a criterion.
    Observable<Integer> values = Observable.create(o -> {
      o.onNext(0);
      o.onNext(10);
      o.onNext(10);
      o.onNext(2);
      o.onComplete();
    });

    Disposable evenNumbers = values
        .all(i -> i % 2 == 0)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e)
        );
    try {System.in.read();} catch (Exception ignore) {}

    // As soon as an item fails the predicate, `false` will be emiited.
    // A value of `true` on the other hand cannot be emitted until the source sequence has completed and `all` of the items are checked
    // Returning the decision inside an observable is a convenient way of making the operation non-blocking

  }

  public void all_fail() {
    Observable<Long> values = Observable.interval(150, TimeUnit.MILLISECONDS).take(5);

    Disposable disposable = values
        .all(i -> i < 3) // Will fail eventually
        .subscribe(
            v -> System.out.println("All: " + v),
            e -> System.out.println("All Error: " + e)
        );

    Disposable disposable2 = values
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );

    try {System.in.read();} catch (Exception ignore) {}
  }

  public void all_error() {
    // If the source observable emits an error, then `all` becomes irrelevant and the error pass through, terminating the sequence
    Observable<Integer> values = Observable.create(o -> {
      o.onNext(0);
      o.onNext(2);
      o.onError(new Exception());
    });

    Disposable disposable = values
        .all(i -> i % 2 == 0)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e)
        );

    try {System.in.read();} catch (Exception ignore) {}
  }

  public void all_fail_before_error() {
    // If, howeve, the predicate fails, then `false` is emitted and the sequence terminates. Even if the source observable fails after that,
    // the event is ignroed, as required by the Rx contract ( no events after a termination event )

    Observable<Integer> values = Observable.create( o -> {
      o.onNext(1);
      o.onNext(2);
      o.onError(new Exception());
    });

    Disposable disposable = values
        .all(i -> i % 2 == 0)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e)
        );
  }

  public void exist() {
    // Change to use `Maybe` in RxJava2 ?
    // The exist method returns an observable that will emit `true` if any of the values emitted by the observable make the predicate true

//    Observable<Integer> values = Observable.range(0, 2);
//
//    Disposable disposable = values
//        .exists(i -> i > 2)
//        .subscribe(
//            v -> System.out.println(v),
//            e -> System.out.println("Error: " + e)
//        );

  }

  public void isEmpty() {
    // This operator's result is a boolean value, indecating if an observable emitted values before completing or not
    Observable<Long> values = Observable.timer(1000, TimeUnit.MILLISECONDS);

    Disposable disposable = values
        .isEmpty()
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e)
        );

    try {System.in.read();} catch (Exception ignore) {}
  }

  public void contains() {
    // `contains` establishes if a particular element is emitted by an observable
    // `contains` will use the `Object.equals` method to establish the quality

    Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

    Disposable disposable = values
        .contains(4L) // if we had used 4 where we used 4L, nothing would be printed. 4 != 4L in java
        .subscribe(
            v -> System.out.println("Contains: " + v),
            e -> System.out.println("Contains Error: " + e)
        );

//    Disposable disposable2 = values
//        .subscribe(
//            v -> System.out.println(v),
//            e -> System.out.println("Error: " + e)
//        );


    try {System.in.read();} catch (Exception ignore) {}
  }

  public void defaultIfEmpty() {
    // rather than checking with `isEmpty`
    // you can force an observable to emit a value on completion if it didn't emit anything before completing
    Observable<Integer> values = Observable.empty();

    Disposable disposable = values
        .defaultIfEmpty(2)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void defaultIfEmpty_error() {
    // the default calue will not be emitted before the error
    Observable<Integer> values = Observable.error(new Exception());

    Disposable disposable = values
        .defaultIfEmpty(2)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void elementAt() {
    // you can select exactly one element out of an observable using the `elementAt` method
    Observable<Integer> values = Observable.range(100, 10);

    Disposable disposable = values
        .elementAt(2)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );

    try {System.in.read();} catch (Exception ignore) {}
  }

  public void elementAtOrError() {
    // to prevent `java.lang.IndexOutOfBoundsException`
    // -> elementAtOrDefault is gone!
    Observable<Integer> values = Observable.range(100, 10);

    Disposable disposable = values
        .elementAtOrError(22)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e)
        );

    try {System.in.read();} catch (Exception ignore) {}
  }

  public void sequenceEqual() {
    // two sequences are equal by comparing the values at hte same index
    // Both the size of the sequences and the values must be equal
    // The function will either use `Object.equals` or the function that you supply to compare values

    Observable<String> strings = Observable.just("1", "2", "3");
    Observable<Integer> ints = Observable.just(1, 2, 3);

    Observable.sequenceEqual(strings, ints, (s, i) -> s.equals(i.toString()))
              //.sequenceEqual(strings, ints) -- result would be false
              .subscribe(
                  v -> System.out.println(v),
                  e -> System.out.println("Error: " + e)
              );

  }

  public void sequenceEqual_error() {
    //failing is not part of the comparision. As soon as either sequence fails, the resulting observable forwards the error
    Observable<Integer> values = Observable.create( o -> {
      o.onNext(1);
      o.onNext(2);
      o.onError(new Exception());
    });

    Observable.sequenceEqual(values, values)
              .subscribe(
                  v -> System.out.println(v),
                  e -> System.out.println("Error: " + e)
              );

  }

  public static void main(String[] args) {
    Sample2_3 sample = new Sample2_3();
    sample.sequenceEqual_error();

  }
}
