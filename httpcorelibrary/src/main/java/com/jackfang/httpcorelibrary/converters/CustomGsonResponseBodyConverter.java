package com.jackfang.httpcorelibrary.converters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import com.jackfang.httpcorelibrary.exceptions.ServerErrorException;
import com.jackfang.httpcorelibrary.model.SuperBaseModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by JackFang on 2017/3/4, 16:28.
 * Project: HttpCore
 * Email: jackfangqi1314@gmail.com
 */
public class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private Gson gson;
    private TypeAdapter<T> adapter;

    public CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        SuperBaseModel superBaseModel = gson.fromJson(response, SuperBaseModel.class);
        if (!superBaseModel.isSuccess()) {
            value.close();
            throw new ServerErrorException(superBaseModel.getMessage(), superBaseModel.getStatus());
        }

        MediaType contentType = value.contentType();
        Charset charset = (contentType == null ? UTF_8 : contentType.charset(UTF_8));
        InputStream in = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(in, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);
        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
