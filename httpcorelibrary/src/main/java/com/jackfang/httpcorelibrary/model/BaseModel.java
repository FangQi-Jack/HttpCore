package com.jackfang.httpcorelibrary.model;

/**
 * Created by JackFang on 2017/3/4, 19:31.
 * Project: HttpCore
 * Email: jackfangqi1314@gmail.com
 */
public class BaseModel<T> extends SuperBaseModel {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
