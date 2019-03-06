package wang.relish.textsample.adapter;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author Relish Wang
 * @since 2019/03/06
 */
public abstract class ObserverAdapter<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
