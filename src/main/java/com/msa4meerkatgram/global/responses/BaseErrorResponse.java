package com.msa4meerkatgram.global.responses;

public record BaseErrorResponse (
    String code
    , String message
) {
    public static BaseErrorResponse from(String code, String message) {
        return new BaseErrorResponse(code, message);
    }
}

