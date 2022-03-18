package ru.dzahbarov.kontur.intern.chartographer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author dzahbarov
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error during updating charta")
public class ChartaUpdatingException extends RuntimeException {
    public ChartaUpdatingException(String message) {
        super(message);
    }
}
