package ru.kontur.chartographer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author dzahbarov
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error during deleting charta")
public class ChartaDeletingException extends RuntimeException {
    public ChartaDeletingException(String message) {
        super(message);
    }
}
