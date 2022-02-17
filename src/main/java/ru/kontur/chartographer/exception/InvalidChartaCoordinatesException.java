package ru.kontur.chartographer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author dzahbarov
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid coordinates for charta")
public class InvalidChartaCoordinatesException extends RuntimeException {
    public InvalidChartaCoordinatesException(String message) {
        super(message);
    }
}
