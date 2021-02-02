import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import model.Person;
import subscriber.PrintSubscriber;

public class Sample2_4 {
    // How we can use the data in the sequence to derive new meaningful values
    // The methods we will see here resemble what is called catamorphism.
    // In our case, it would mean that the methods consume the values in the sequence and compose them into one
    // However, they do not strictly meet the definition, as they don't return a single value. Rather, they return an observable that promises to emit a single value

    public void count() {
        Observable<Integer> values = Observable.range(0,3);

        values
                .subscribe(new PrintSubscriber("Values"));

        values
                .count()
                .subscribe(new PrintSubscriber("Count"));

    }

    public void first() {
        // `first` will return an observable that emits only the first value in sequence
        // It is similar to `take(1)`, except that it will emit `java.util.NoSuchElementException` if noe is found.
        // If you use the overload that takes a predicate, the first value that matches the predicate is returned
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values
                .first(1000L)
                .subscribe(new PrintSubscriber("First"));

    }

    public void last() {
        // `last` and `lastOrDefault` work in the same way as `first`,
        // except that the item returned is the last item before sequence completed
        Observable<Integer> values = Observable.range(0, 10);

        values
                .last(1234)
                .subscribe(new PrintSubscriber("Last"));

    }

    public void single() {
        // `single` emits the only value in the sequence, or the only value that met predicate when is given.
        // It differs from `first` and `last` in that it does not ignore multiple matches.
        // If multiple matches are found, it will emit an error. It can be used to assert that a sequence must only contain one such value.
        // Remember that `single` must check the entire sequence to ensure your assertion

        //Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);
        Observable<Integer> values = Observable.range(0,0);
        //Observable<Integer> values = Observable.range(0,1);

        values.take(10)
              .single(5) // Emits a result
              .subscribe(new PrintSubscriber("Single1"));

        values.take(3)
              .single(6)
              .subscribe(new PrintSubscriber("Single2"));

    }

    public void reduce() {
        // The general idea is that you produce a single value out of many by combining them two at a time
        // In its most basic overload, all you need is a function that combines two values into one.
        // In example, Here we will calculate the sum of a sequence of integers: 0+1+2+3+4+.... We will also calculate the minimum value for a different example

        // 0 1 2 3 4
        Observable<Integer> values = Observable.range(0,5);
        values
                .reduce((i1, i2) -> i1+i2)
                .subscribe(new PrintSubscriber("Sum"));

        values
                .reduce((i1,i2) -> (i1>i2) ? i2 : i1)
                .subscribe(new PrintSubscriber("Min"));

        //Each time, the accumulator function combines the result of the previous step with the next value.
        // This is more obvious in another overload
        // public final <R> Observable<R> reduce(R initialValue, Func2<R, ? super T,R> accumulator)

    }

    public void count_via_reduce() {
        Observable<String> values = Observable.just("Rx", "is", "easy");

        values
                .reduce(0, (acc, next) -> acc + 1)
                .subscribe(new PrintSubscriber("Count"));


        // We start with an accumulator of 0, as we have counted 0 items.
        // Every time a new item arrives, we return a new accumulator that is increased by one.
        // The Last value corresponds to the number of elements in the source sequence

        // `reduce` can be used to implement the functionality of most of the operators that emit a single value.
        // It can not implement behaviour where a value is emitted before the source completes.
        // So, you can implement `last` using `reduce`, but an implementation of `all` would not behave exactly like the original

        values
                .reduce((i1,i2) -> i2)
                .subscribe(new PrintSubscriber("Last"));
    }

    public void scan() {
        // `scan` is very similar to `reduce`, with the key difference being that `scan` will emit all the intermediate result
        // In the case of our example for a sum, using `scan` will produce a running sum

        Observable<Integer> values = Observable.range(0, 5);

        values
                .scan((i1, i2) -> i1+i2)
                .subscribe(new PrintSubscriber("Sum"));

        // `scan` is more general than `reduce`, since `reduce` can be implemented with `scan: reduce(acc) = scan(acc).takeLast()`

    }

    public void scan_minimum() {
        // `scan` emits when the source emits and does not need the source to complete.
        // We demonstrate that by implementing an observable that returns a running minimu,:
        Subject<Integer> values = ReplaySubject.create();

        values
                .subscribe(new PrintSubscriber("Values"));

        values
                .scan((i1,i2)->(i1<i2) ? i1: i2)
                .distinctUntilChanged()
                .subscribe(new PrintSubscriber("Min"));

        values.onNext(2);
        values.onNext(3);
        values.onNext(1);
        values.onNext(4);
        values.onComplete();
    }

    //Aggregation to collections
    public void aggregate() {
        // In `reduce` nothing is stopping your accumulator from being a collection
        // you can use `reduce` to collect every value in `Observable<T>` into a `List<T>`

        Observable<Integer> values = Observable.range(10, 5);

        values
                .reduce(
                        new ArrayList<Integer>(),
                        (acc, value) -> {
                            acc.add(value);
                            return acc;
                        })
                .subscribe(v -> System.out.println(v));
    }

    public void aggregate_right() {
        // `aggregate()` has a problem formality: `reduce` is meant to be a functional fold and such folds are not supposed to work on mutable accumulators.
        // If we were to do this the "right" way, we would have to create a new instance of `ArrayList<Integer>` for every new item, like this
        Observable<Integer> values = Observable.range(10, 5);

        values
                .reduce(
                        new ArrayList<Integer>(),
                        (acc, value) -> {
                            ArrayList<Integer> newAcc = (ArrayList<Integer>) acc.clone();
                            newAcc.add(value);
                            return newAcc;
                        }
                ).subscribe(v -> System.out.println(v));
    }

    public void collect() {
        // The performance of creating a new collection for every new item is unacceptable. For that reason, Rx offers the `collect` operator,
        // which does the same thing as `reduce`, only using a mutable accumulator this time
        // By using `collect` you document that you are not following the convention of immutability and you also simplify tour code a little:
        Observable<Integer> values = Observable.range(10, 5);

        values
                .collect(
                        () -> new ArrayList<Integer>(),
                        (acc, value) -> acc.add(value))
                .subscribe(v -> System.out.println(v));
    }

    public void toList() {
        Observable<Integer> values = Observable.range(10, 5);

        values
                .toList()
                .subscribe(v -> System.out.println(v));
    }

    public void toSortedList() {
        Observable<Integer> values = Observable.range(10, 5);

        values
                .toSortedList((i1,i2) -> i2 - i1)
                .subscribe(v -> System.out.println(v));
    }

    // `keySelector` is a function that produces a key from a value
    // `valueSelector` produces from the emitted value the actual value that will be stored in the map
    // `mapFactory` creates the collection that will hold the items
    public void toMap_simple() {
        Observable<Person> values = Observable.just(
                new Person("Will", 25),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values
                .toMap(person -> person.name)
                .subscribe(new PrintSubscriber("toMap"));
    }

    public void toMap_key_value() {
        Observable<Person> values = Observable.just(
                new Person("Will", 25),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values
                .toMap(
                        person -> person.name,
                        person -> person.age)
                .subscribe(new PrintSubscriber("toMap"));
    }

    public void toMap_key_value_container() {
        Observable<Person> values = Observable.just(
                new Person("Will", 25),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values
                .toMap(
                        person -> person.name,
                        person -> person.age,
                        () -> new HashMap<>())
                .subscribe(new PrintSubscriber("toMap"));

        // The container is provided as a factory function because a new container needs to be created for every new subscription
    }

    // When mapping, it is very common that many values share the same key.
    // The datastrcuture that maps one key to multiple values is called a multimap and it is a map from keys to collections.
    // This process can also be called "grouping"

    public void multiMap_grouping() {
        Observable<Person> values = Observable.just(
                new Person("Will", 35),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values
                .toMultimap(
                        person -> person.age,
                        person -> person.name
                )
                .subscribe(new PrintSubscriber("toMap"));
    }

    public void multiMap_container() {
        // The fourth allows us to provide not only the `Map` but also the `Collection` that the values will be stored in
        // The key is provided as a parameter, in case we want to customise the corresponding collection based on key
        // This example we'll just ignore it
        Observable<Person> values = Observable.just(
                new Person("Will", 35),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values
                .toMultimap(
                        person -> person.age,
                        person -> person.name,
                        () -> new HashMap(),
                        (key) -> new ArrayList()
                ).subscribe(new PrintSubscriber("toMap"));

        // The operators just presented have actually limited use.
        // It is tempting for a beginner to collect the data in a collection and process them in the traditional way.
        // That should be avoided not just for didactic purpose, but because this practice defeats the advantages of using Rx in the first place

    }

    public void groupBy() {
        // The last general function that we will see for now is `groupBy`
        // It is the Rx way of doing `toMultimap`
        // For each value, it calculates a key and groups the values into seperate observables based on that key

        //The return value is an observable of `GroupObservable`

        // The nested observables may complicate the signature, but they offer the advantage of allowing the groups to start emitting their items before the source observable has completed

        // In example, we will take a set of words and, for each starting letter, we will print the last word that occured

        Observable<String> values = Observable.just(
                "first",
                "second",
                "third",
                "forth",
                "fifth",
                "sixth"
        );

        values.groupBy(word -> word.charAt(0))
              .subscribe(
                      group -> group.last("last")
                      .subscribe(v -> System.out.println(group.getKey() + ": " + v))
              );
    }

    public void flatMap() {
        Observable<String> values = Observable.just(
                "first",
                "second",
                "third",
                "forth",
                "fifth",
                "sixth"
        );

//        values.groupBy(word -> word.charAt(0))
//              .flatMap(group -> group.last("last")
//                                     .map(v -> group.getKey() + ": " + v))
//              .subscribe(v -> System.out.println(v));

        //FIXME
        values.groupBy(word -> word.charAt(0))
              //.flatMap((groupedObservable, mapper) -> mapper.)
              .subscribe(v -> System.out.println(v));
    }


    public static void main(String[] args) {
        Sample2_4 sample = new Sample2_4();
        sample.groupBy();
        try {System.in.read();} catch (Exception ignore) {}

    }
}
