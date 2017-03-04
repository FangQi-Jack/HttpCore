package com.jackfang.httpcorelibrary.rx;

import com.jackfang.httpcorelibrary.exceptions.ServerErrorException;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by JackFang on 2017/3/4, 17:05.
 * Project: HttpCore
 * Email: jackfangqi1314@gmail.com
 */
public abstract class RxException<T extends Throwable> implements Consumer<T> {
    private static final String TAG = "RxException";

    public static final int NETWORK_ERROR_EXCEPTION_CODE = -1;
    private static final String NETWORK_ERROR_EXCEPTION_MSG = "网络错误";

    @Override
    public void accept(@NonNull T t) throws Exception {
        if (t instanceof ServerErrorException) {
            ServerErrorException see = (ServerErrorException) t;
            acceptError(see.getErrorCode(), see.getMessage());
        } else {
            acceptError(NETWORK_ERROR_EXCEPTION_CODE, NETWORK_ERROR_EXCEPTION_MSG);
        }
    }

    public abstract void acceptError(int errorCode, String errorMessage);
}
