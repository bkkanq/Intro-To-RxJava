import java.io.IOException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class Sample2_1 {
  /*
  * In most cases,
  * "subject"s are not the best way to create a new Observable.
  * We will now see tidier ways to create observable sequences.
* */

  public void factory_observable_just() {
    Observable<String> values = Observable.just("one", "two", "three");
    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: " + e),
        () -> System.out.println("Completed")
    );
  }

  public void factory_observable_empty() {
    //This observable will emit a single onCompleted and nothing else
    Observable<String> values = Observable.empty();
    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: " + e),
        () -> System.out.println("Completed")
    );
  }

  public void factory_observable_never() {
    //This observable wil never emit anything
    Observable<String> values = Observable.never();
    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: " + e),
        () -> System.out.println("Completed")
    );

    //the code above will print nothing
    //note that this doesn't mean that the program is blocking
    //in fact, it will terminate immediately.
  }

  public void factory_observable_error() {
    //This observable will emit a single error event and terminate
    Observable<String> values = Observable.error(new Exception("Oops!"));
    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: " + e),
        () -> System.out.println("Completed")
    );
  }

  public void factory_observable_defer_explain_via_just() {
    //defer doesn't define a new kind of observable, but allows you to declare how an observable should be created every time a subscriber arrives.
    //Consider how you would create an observable that returns the current time and terminates.
    //You are emitting a single value, so it sounds like a case for just

    Observable<Long> now = Observable.just(System.currentTimeMillis());

    now.subscribe(System.out::println);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignore) {}
    now.subscribe(System.out::println);
  }

  public void factory_observable_defer() {
    Observable<Long> now = Observable.defer(() ->
        Observable.just(System.currentTimeMillis()));

    now.subscribe(System.out::println);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignore) {}
    now.subscribe(System.out::println);
  }

  public void factory_observable_create() {
    Observable<String> values = Observable.create( o -> {
      o.onNext("Hello");
      o.onComplete();
    });
    //Observable<String> same = Observable.just("hello");

    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: " + e),
        () -> System.out.println("Completed")
    );

    // This method should be your preffered way of creating a custom observable, when none of the existing shorthands serve your purpose.
    // The code is similar to how we created a Subject and pushed values to it, but there are a few important differences.
    // First of all, the source of the events is neatly encapsulated and separated from unrelated code.
    // Secondly, `Subject`s carry dangers that are not obvious : with a `Subject` you are managing state, and anyone with access to the instance can push values into it and alter the sequence.
    // We wil see more about this issue later on.
    // Another key difference to using subjects is that the code is executed lazily, when and if an observer subscribes.
    // In the example above, the code is run not when the observable is created(because there is no `Subscriber` yet), but each time `subscriber`is called
    // This means that every values is generated again for each subscriber, similar to `ReplaySubject`
    // The end result is similar to `ReplaySubject`, exept that no caching takes place
    // However, if we had used a `ReplaySubject`, and the cration method was time-consuming, that would block the thread that executes the creation.
    // You'd have to manually create a new thread to push values into `Subject`.
    // We're not presenting Rx's methods for concurrency yet, but there are convenient ways to make the execution of the `onSubscribe` function concurrently

    // You may have already noticed that you can trivially implement any of the previous observables using Observable.create.
    // Infact, our example for `create` is equivalent to `Observable.just("hello")`
  }

  // In functional programming it is common to create sequences of unrestricted or infinite length
  // RxJava has factory methods that create such sequences
  public void functional_unfolds_observable_range() {
    // A straight forward and familiar method to any functional programmer. It emits the specified range of integers.
    Observable<Integer> values = Observable.range(10, 15);
    values.subscribe(System.out::println);

  }

  public void functional_unfolds_observable_interval_1() {
    // This function will create an infinite sequence of ticks, seperated by the specified time duration
    Observable<Long> values = Observable.interval(1000, TimeUnit.MILLISECONDS);
    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: "+ e),
        () -> System.out.println("Completed")
    );
    try {
      System.in.read();
    } catch (IOException ignore) {
      System.out.println("Interrupted !");
    }

    // This sequence will not terminate until we unsubscribe
    // We should note why the blocking read at the end is necessary.
    // Without it, the program terminates without printing something. That's because our operations are non-blocking:
    // we create an observable that will emit values over time, then we register the actions to execute if and when values arrive.
    // None of that is blocking and the main thread proceeds to terminate. The timer that produces the ticks runs on its own thread,
    // which does not prevent the JVM from terminating. killing the timer with it.
  }

  public void functional_unfolds_observable_timer_1() {
    // This example creates an observable that waits a given amount of time, then emits 0L and terminates.
    Observable<Long> values = Observable.timer(1, TimeUnit.SECONDS);
    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: " + e),
        () -> System.out.println("Completed")
    );

    try {
      System.in.read();
    } catch (IOException ignore) {
      System.out.println("Interrupted !");
    }


  }

  public void functional_unfolds_observable_interval_2() {
    // This example will wait a specified amount of time, then begin emitting like interval with the given frequency
    Observable<Long> values = Observable.interval(2, 1, TimeUnit.SECONDS);
    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: " + e),
        () -> System.out.println("Completed")
    );

    try {
      System.in.read();
    } catch (IOException ignore) {
      System.out.println("Interrupted !");
    }

  }


//  public void transitioning_into_observable_eventhandler() {
//    Observable<ActionEvent> events = Observable.create( o -> {
//      button2.setOnAction(new EventHandler<ActionEvent>() {
//        @Override public void handle(ActionEvent e) {
//          o.onNext(e)
//        }
//      });
//    });
//  }

  public void transitioning_into_observable_fromFuture() {
    // Much like most of the functions we've seen so far, you can turn any kind of input into an Rx observable with create.
    // There are several shorthands for converting common types of input

    FutureTask<Integer> f = new FutureTask<Integer>(() -> {
      Thread.sleep(2000);
      return 21;
    });
    new Thread(f).start();

    Observable<Integer> values = Observable.fromFuture(f);

    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: " + e),
        () -> System.out.println("Completed")
    );

    // The observable emits the result of the FutureTask when it is available and the terminates.
    // If the task is canceled, the observable will emit a `java.util.concurrent.CancellationException` error.

    // If you're interested in the results of the `Future` for a limited amount of time, you can provide a timeout period like this
    // Observable<Integer> values = Observable.fromFuture(f, 1000, TimeUnit.MICROSECONDS);
  }

  public void transitioning_into_observable_fromArray() {
    // You can also turn any collection into an observable using the overloads of Observable.from that take arrays and iterables.
    // This will result in every item in the collection being emitted and then a final onCompleted event
    Integer[] is = {1, 2, 3};
    Observable<Integer> values = Observable.fromArray(is);
    Disposable disposable = values.subscribe(
        v -> System.out.println("Received: " + v),
        e -> System.out.println("Error: " + e),
        () -> System.out.println("Completed")
    );
  }

  // `Observable` is not interchangeable with `Iterable` or `Stream`.
  // `Observable`s are push-based, i.e., the call to `onNext` causes the stack of handlers to execute all the way to the final subscriber method (unless specified otherwise).
  // The other models are pull-based, which means that values are requested as soon as possible and execution blocks until the result is returned.
  public static void main(String[] args) {
    Sample2_1 sample = new Sample2_1();
    sample.transitioning_into_observable_fromArray();

  }
}
