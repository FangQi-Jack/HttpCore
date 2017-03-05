package com.jackfang.httpcorelibrary.rx;

import com.jackfang.httpcorelibrary.model.BaseModel;

import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
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
                        return createData(tBaseModel.getData());
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> FlowableTransformer<BaseModel<T>, T> io_main_flowable() {
        return new FlowableTransformer<BaseModel<T>, T>() {
            @Override
            public Publisher<T> apply(Flowable<BaseModel<T>> upstream) {
                return upstream.flatMap(new Function<BaseModel<T>, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(@NonNull BaseModel<T> tBaseModel) throws Exception {
                        return createDataFlowable(tBaseModel.getData());
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

    private static <T> Flowable<T> createDataFlowable(final T t) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> subscriber) throws Exception {
                try {
                    subscriber.onNext(t);
                    subscriber.onComplete();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }, BackpressureStrategy.BUFFER);
    }

}
