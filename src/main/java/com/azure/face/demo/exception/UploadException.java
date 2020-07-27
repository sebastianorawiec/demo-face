package com.azure.face.demo.exception;

import lombok.Data;

@Data
public class UploadException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String message;

    public UploadException(String msg) {
        this.message = msg;

    }

}

