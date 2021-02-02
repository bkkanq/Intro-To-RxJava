package subscriber;

import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PrintSubscriber implements Observer, SingleObserver, MaybeObserver {
    private final String name;

    public PrintSubscriber(String name) {
        this.name = name;
    }
    @Override
    public void onSubscribe(final Disposable d) {

    }

    @Override
    public void onSuccess(final Object o) {
        System.out.println(name + ": Success: " + o);
    }

    @Override
    public void onNext(final Object o) {
        System.out.println(name + ": " + o);

    }

    @Override
    public void onError(final Throwable e) {
        System.out.println(name + ": Error: " + e);

    }

    @Override
    public void onComplete() {
        System.out.println(name + ": Completed");

    }
}
