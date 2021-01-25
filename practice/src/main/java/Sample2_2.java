import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class Sample2_2 {
  //Reducing a sequence
  public void filter() {
    // If the decision is false, the item is ommited from the filtered sequence.
    Observable<Integer> values = Observable.range(0, 10);
    Disposable oddNumbers = values
        .filter(v -> v % 2 == 0)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void distinct() {
    Observable<Integer> values = Observable.create(o -> {
      o.onNext(1);
      o.onNext(1);
      o.onNext(2);
      o.onNext(3);
      o.onNext(2);
      o.onComplete();
    });

    Disposable disposable = values
        .distinct()
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void distinct_keySelector() {
    //An overload of distinct takes a key selector. For each item, the function generates a key and the key is then used to determine distinctiveness
    Observable<String> values = Observable.create(o -> {
      o.onNext("First");
      o.onNext("Second");
      o.onNext("Third");
      o.onNext("Fourth");
      o.onNext("Fifth");
      o.onComplete();
    });

    Disposable disposable = values
        .distinct(v -> v.charAt(0))
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void distinct_untilChanged() {
    // The difference is that only consecutive non-distinct values are filtered out
    Observable<Integer> values = Observable.create(o -> {
      o.onNext(1);
      o.onNext(1);
      o.onNext(2);
      o.onNext(3);
      o.onNext(2);
      o.onComplete();
    });

    Disposable disposable = values
        .distinctUntilChanged()
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void distinct_untilChanged_keySelector() {
    Observable<String> values = Observable.create(o -> {
      o.onNext("First");
      o.onNext("Second");
      o.onNext("Third");
      o.onNext("Fourth");
      o.onNext("Fifth");
    });

    Disposable disposable = values
        .distinctUntilChanged(v -> v.charAt(0))
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );

  }

  public void ignoreElements() {
    //ignoreElement will ignore every value, but lets pass through onCompleted and onError
    Observable<Integer> values = Observable.range(0, 10);
    Disposable disposable = values
        .ignoreElements()
        .subscribe(
            () -> System.out.println("Completed"),
            e -> System.out.println("Error: " + e)
        );

    //ignoreElements() produces the same results as `filter(v -> false)`
  }

  public void take() {
    // The next group of methods serve to cut the sequence at a specific point based on the item's index,
    // and either take the first part or the second part
    // `take` takes the first n elements, while `skip` skips them.
    // Note that neither function considers it an error if there are fewer items in the sequence than the specified index.

    Observable<Integer> values = Observable.range(0, 5);
    Disposable take2 = values
        .take(2)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void take_with_error() {
    Observable<Integer> values = Observable.create(o -> {
      o.onNext(1);
      o.onError(new Exception("Oops"));
    });

    Disposable disposable = values
        .take(1)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void skip() {
    // `skip` returns the other half of a take
    Observable<Integer> values = Observable.range(0, 5);
    Disposable disposable = values
        .skip(2)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void take_moment() {
    // There are overloads where the cutoff is a moment in time ather than place in the sequence
    Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

    Disposable disposable = values
        .take(250, TimeUnit.MILLISECONDS)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );

    try{System.in.read();} catch(Exception ignore){}
  }

  public void takeWhile() {
    //`take` and `skip` work with predefined indices. If you want to "discover" the cutoff point as the values come,
    //`takeWhile` and `skipWhile` will use a predicate instead. `takeWhile` takes items while a predicate function returns `true`
    Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

    Disposable disposable = values
        .takeWhile(v -> v < 2)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );

    try{System.in.read();} catch(Exception ignore){}
  }

  public void skipWhile() {
    Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

    Disposable disposable = values
        .skipWhile(v -> v < 2)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );

    try{System.in.read();} catch(Exception ignore){}

  }

  public void skipLast() {
    //`skipLast` and `takeLast` work just like `take` and `skip`, with the difference that the point of reference is from the end
    Observable<Integer> values = Observable.range(0, 5);

    Disposable disposable = values
        .skipLast(2)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );
  }

  public void takeUntil() {
    // The cutoff point is defined as the moment when another obervable emits an item
    Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);
    Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS);

    Disposable disposable = values
        .takeUntil(cutoff)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );

    try{System.in.read();} catch(Exception ignore){}
  }

  public void skipUntil() {
    Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);
    Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS);

    Disposable disposable = values
        .skipUntil(cutoff)
        .subscribe(
            v -> System.out.println(v),
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );

    try{System.in.read();} catch(Exception ignore){}


  }

  public static void main(String[] args) {
    Sample2_2 sample = new Sample2_2();
    sample.skipUntil();
  }
}
