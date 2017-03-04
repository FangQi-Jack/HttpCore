package com.jackfang.httpcorelibrary.exceptions;

/**
 * Created by JackFang on 2017/3/4, 16:31.
 * Project: HttpCore
 * Email: jackfangqi1314@gmail.com
 */
public class ServerErrorException extends RuntimeException {
    private int errorCode;

    public ServerErrorException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
