package ru.dzahbarov.kontur.intern.chartographer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author dzahbarov
 */

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Charta during processing uploaded charta")
public class ChartaUploadingException extends RuntimeException {
    public ChartaUploadingException(String message) {
        super(message);
    }
}
