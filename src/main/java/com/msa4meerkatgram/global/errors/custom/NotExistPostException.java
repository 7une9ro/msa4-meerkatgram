package com.msa4meerkatgram.global.errors.custom;

public class NotExistPostException extends RuntimeException {

    public NotExistPostException(String message) {
        super(message);
    }
}
