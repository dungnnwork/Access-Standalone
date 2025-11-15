package com.example.test_pro.data.network;

public interface ApiCallback<T> {
    void onSuccess(T data);
    void onError(Throwable t);
}
