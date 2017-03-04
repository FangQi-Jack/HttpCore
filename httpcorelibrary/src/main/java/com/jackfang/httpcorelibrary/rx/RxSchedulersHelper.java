package com.jackfang.httpcorelibrary.rx;

import com.jackfang.httpcorelibrary.exceptions.ServerErrorException;
import com.jackfang.httpcorelibrary.model.BaseModel;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by JackFang on 2017/3/4, 16:51.
 * Project: HttpCore
 * Email: jackfangqi1314@gmail.com
 */
public class RxSchedulersHelper {
    private static final String TAG = "RxSchedulersHelper";

    public static <T> ObservableTransformer<BaseModel<T>, T> io_main() {
        return new ObservableTransformer<BaseModel<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<BaseModel<T>> upstream) {
                return upstream.flatMap(new Function<BaseModel<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(@NonNull BaseModel<T> tBaseModel) throws Exception {
                        if (tBaseModel.isSuccess()) {
                            return createData(tBaseModel.getData());
                        } else {
                            return Observable.error(new ServerErrorException(tBaseModel.getMessage(),
                                    tBaseModel.getStatus()));
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    private static <T> Observable<T> createData(final T t) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                try {
                    subscriber.onNext(t);
                    subscriber.onComplete();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
