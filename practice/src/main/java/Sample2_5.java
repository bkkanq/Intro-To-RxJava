import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import subscriber.PrintSubscriber;

public class Sample2_5 {

    public void map() {
        Observable<Integer> values = Observable.range(0, 4);

        values.map(i -> i + 3)
              .subscribe(new PrintSubscriber("Map"));
    }

    public void map_practical() {
        Observable<Integer> values = Observable.just("0", "1", "2", "3")
                .map(Integer::parseInt);

        values.subscribe(new PrintSubscriber("Map"));
    }

    public void cast() {
        Observable<Object> values = Observable.just(0, 1, 2, 3);

        values
                .cast(Integer.class)
                .subscribe(new PrintSubscriber("Map"));
    }

    public void cast_fail() {
        Observable<Object> values = Observable.just(0, 1, 2, "3");

        values
                .cast(Integer.class)
                .subscribe(new PrintSubscriber("Map"));
    }

    public void ofType() {
        Observable<Object> values = Observable.just(0, 1, "2", 3);

        values
                .ofType(Integer.class)
                .subscribe(new PrintSubscriber("Map"));
    }

    //timestamp 는 나중에..

    public void materialize() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(3)
              .materialize()
              .subscribe(new PrintSubscriber("Materialize"));

        // `dematerialize` will reverse the effect of `materialize`, returning a materialized observable to its normal form
    }

    public void flatMap_1() {
        // `map` took one value and returned another, replacing items in the sequence one-for-one.
        // `flatMap` will replace an item with any number of items, including zero or infinite items.
        // `flatMap`'s transformation method takes values from the source observable and, for each of them, returns a new observable that emits the new values

        // The observable returned by `flatMap` will emit all the values emitted by all the observables produced by the transformation function.
        // Values from the same observable will be in order, but they may be interleaved with values from other observables.

        // example, `flatMap` is applied on an observable with a single value.
        // `values` will emit a single value, `2`. `flatMap` will turn it into an observable that is the range between 0 and 2.
        // The values in this observable are emitted in the final observable
        Observable<Integer> values = Observable.just(2);

        values
                .flatMap(i -> Observable.range(0,2))
                .subscribe(new PrintSubscriber("flatMap"));
    }

    public void flatMap_2() {
        // When `flatMap` is applied on an observable with multiple values, each value will produce a new observable.
        // `values1 will emit `1`, `2` and `3`. The resulting observables will emit the values `[0]`, `[0,1]`, `[0,1,2]`, respectively.
        // The values will be flattened together into one observable: the one that is returned by `flatMap`

        Observable<Integer> values = Observable.range(1,3);

        values
                .flatMap(i -> Observable.range(0,i))
                .subscribe(new PrintSubscriber("flatMap"));
    }

    public void flatMap_3() {
        // Much like `map`, `flatMap`'s input and output type are free to differ.
        // In the next example, we will transform integers into `Character`

        Observable<Integer> values = Observable.just(1);

        values
                .flatMap(i ->
                        Observable.just((char)(i+64))
                        )
                .subscribe(new PrintSubscriber("flatMap"));
    }

    public void flatMap_4() {
        // This hasn't helped us more than `map` operator. There is one key difference that we can exploit to get more out of the `flatMap` operator
        // While every value must result in a `Observable`, nothing prevents this observable from being empty.
        // We can use that to silenty filter the sequence while transforming it at the same time

        Observable<Integer> values = Observable.range(0, 30);

        values
                .flatMap(i -> {
                    if (0 < i &&i <= 26)
                        return Observable.just(Character.valueOf((char)(i+64)));
                    else
                        return Observable.empty();
                })
                .subscribe(new PrintSubscriber("flatMap"));
    }

    public void flatMap_5() {
        // In our examples for `flatMap` so far, the values where in sequence: first all the values from the first observable,
        // then all the values from the second observable.
        // Though this seems intuitive, especially when coming from a synchronous environment, it is important to note that this is not always the case
        // The observable returned by `flatMap` emits values as soon as they are available.
        // It just happened that in our examples, all of the observables had all of their values ready ssynchronously.
        // To demonstrate, we construct asynchronous observables using the `interval` method

        Observable.just(100, 150)
                  .flatMap( i ->
                          Observable.interval(i, TimeUnit.MILLISECONDS)
                            .map(v -> i)
                  )
                  .take(10)
                  .subscribe(new PrintSubscriber("flatMap"));

        // We started with the values 100 and 150, which we used as the interval period for the asynchronous observable created in `flatMap`
        // Since `interval` emits the numbers 1,2,3... in both cases, to better distinguish the two observables, we replaced those values with interval time that each observable operates on
   }

   public void concatMap() {
        // Even though `flatMap` share its name with a very common operator in functional programming, we saw that it doesn't behave exactly like a functional progammer would expect
       // `flatMap` may interleave the supplied sequences. There is an operator that won't interleave the sequences and is called `concatMap`, because it is related to the concat operator that we will see later

       Observable.just(100, 150)
                 .concatMap( i ->
                         Observable.interval(i, TimeUnit.MILLISECONDS)
                             .map(v -> i)
                             .take(3)
                 )
                 .subscribe(
                        new PrintSubscriber("concatMap")
                 );

       // We can see in the output that the two sequences are kept seperate
       // Note that the `concatMap` operator only works with terminating sequences: it can't move on to the next sequence before the current sequence terminates
       // For that reason, we had to limit `interval`'s infinite sequence with `take`


   }


    public static void main(String[] args) {
        Sample2_5 sample = new Sample2_5();
        sample.concatMap();

        try {System.in.read();} catch (Exception ignore) {}

    }
}
