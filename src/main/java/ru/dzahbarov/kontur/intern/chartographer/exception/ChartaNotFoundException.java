package ru.dzahbarov.kontur.intern.chartographer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author dzahbarov
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Charta not found")
public class ChartaNotFoundException extends RuntimeException {
    public ChartaNotFoundException(String message) {
        super(message);
    }
}
