package ru.kontur.chartographer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author dzahbarov
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error during creating charta")
public class ChartaCreatingException extends RuntimeException{
    public ChartaCreatingException(String message) {
        super(message);
    }
}
