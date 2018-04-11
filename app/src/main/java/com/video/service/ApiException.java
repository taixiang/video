package com.video.service;

/**
 * Created by tx on 2017/9/26.
 * 非成功状态码抛出
 */

public class ApiException extends RuntimeException {

    private int errorCode;

    public ApiException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
