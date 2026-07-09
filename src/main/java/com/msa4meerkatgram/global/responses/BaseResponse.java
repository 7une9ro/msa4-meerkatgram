package com.msa4meerkatgram.global.responses;

public record BaseResponse<T>(
    String code
    , String message
    , T data
) {
    public static <T> BaseResponse<T> from(String code, String message, T data) {
        return new BaseResponse<T>(code, message, data);
    }
}
