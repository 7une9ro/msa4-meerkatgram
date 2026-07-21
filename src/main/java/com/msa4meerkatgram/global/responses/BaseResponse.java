package com.msa4meerkatgram.global.responses;

import com.msa4meerkatgram.global.responses.constant.CustomResponseCode;

public record BaseResponse<T>(
    String code
    , String message
    , T data
) {
    public static <T> BaseResponse<T> from(CustomResponseCode customResponseCode, T data) {
        return new BaseResponse<T>(customResponseCode.getCode(), customResponseCode.name(), data);
    }

    public static BaseResponse<Void> from(CustomResponseCode customResponseCode) {
        return new BaseResponse<Void>(customResponseCode.getCode(), customResponseCode.name(), null);
    }

    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>from(CustomResponseCode.SUCCESS, data);
    }

    public static BaseResponse<Void> success() {
        return BaseResponse.<Void>from(CustomResponseCode.SUCCESS);
    }
}
