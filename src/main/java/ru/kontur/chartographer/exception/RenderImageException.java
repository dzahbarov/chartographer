package ru.kontur.chartographer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author dzahbarov
 */

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error during rendering image")
public class RenderImageException extends RuntimeException {
    public RenderImageException(String message) {
        super(message);
    }

    public RenderImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public RenderImageException(Throwable cause) {
        super(cause);
    }

    public RenderImageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RenderImageException() {
    }
}
