import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

public class Sample1_3 {
  public void error() {
    Subject<Integer> s = ReplaySubject.create();
    s.subscribe(
        v -> System.out.println(v),
        e -> System.out.println(e));
    s.onNext(0);
    s.onError(new Exception("Oops"));
  }

  public void unsubscribing_1() {
    Subject<Integer> values = ReplaySubject.create();
    Disposable disposable = values.subscribe(
        v -> System.out.println(v),
        e -> System.out.println(e),
        () -> System.out.println("Done")
    );
    values.onNext(0);
    values.onNext(1);
    disposable.dispose();
    values.onNext(2);

  }

  public void unsubscribing_2() {
    Subject<Integer> values = ReplaySubject.create();
    Disposable disposable1 = values.subscribe(
        v -> System.out.println("First: " +v)
    );
    Disposable disposable2 = values.subscribe(
        v -> System.out.println("Second: " +v)
    );
    values.onNext(0);
    values.onNext(1);
    disposable1.dispose();
    System.out.println("Unsubscribed first");
    values.onNext(2);
  }

  public void onErrorAndonCompleted() {
    Subject<Integer> values = ReplaySubject.create();
    Disposable disposable1 = values.subscribe(
        v -> System.out.println("First: "+ v),
        e -> System.out.println("First: "+ e),
        () -> System.out.println("Completed")

    );

    values.onNext(0);
    values.onNext(1);
    values.onComplete();
    values.onNext(2);
  }

  public void freeingResources() {
    /*
     * Not working on RxJava2
     */

//    Subscription s = Subscriptions.create(() -> System.out.println("Clean"));
//    s.unsubscribe();

    //Disposable.fromAction takes an action that will be executed on unsubscription to release the resources
    // There also are shorthand for common actions when creating a sequence.
    Disposable s = Disposables.fromAction(() -> System.out.println("Clean"));
    s.dispose();

    Disposables.disposed();

  }

  public static void main (String[] args) {
    Sample1_3 sample = new Sample1_3();
    sample.freeingResources();

  }
}
