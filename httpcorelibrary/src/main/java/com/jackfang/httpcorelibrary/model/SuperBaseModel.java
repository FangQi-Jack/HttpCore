package com.jackfang.httpcorelibrary.model;

import com.jackfang.httpcorelibrary.Constants;

/**
 * Created by JackFang on 2017/3/4, 19:30.
 * Project: HttpCore
 * Email: jackfangqi1314@gmail.com
 */
public class SuperBaseModel {
    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return Constants.HTTP_SUCCESS_CODE == status;
    }
}
