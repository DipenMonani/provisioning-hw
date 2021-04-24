package com.voxloud.provisioning.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GenericServerException extends RuntimeException {
	public GenericServerException(String message) {
        super(message);
    }
}
