package ru.kontur.chartographer.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kontur.chartographer.service.ChartaService;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

/**
 * @author dzahbarov
 */

@Validated
@ControllerAdvice
@RestController
@RequestMapping("chartas")
public class ChartographerController {
    private final ChartaService chartaService;

    public ChartographerController(ChartaService chartaService) {
        this.chartaService = chartaService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public String createImage(@RequestParam @Positive @Max(20_000) Integer width,
                              @RequestParam @Positive @Max(50_000) Integer height) {
        return Long.toString(chartaService.createImage(width, height).getId());
    }

    @PostMapping(value = "{id}")
    public void updateImage(@PathVariable long id,
                            @RequestParam int x,
                            @RequestParam int y,
                            @RequestParam @Positive @Max(20_000) int width,
                            @RequestParam @Positive @Max(50_000) int height,
                            @RequestParam(value = "image") MultipartFile image) {
        chartaService.updateCharta(id, x, y, width, height, image);
    }

    @GetMapping(value = "{id}", produces = "image/bmp")
    public Resource getImage(@PathVariable long id,
                             @RequestParam int x,
                             @RequestParam int y,
                             @RequestParam @Positive @Max(5000) int width,
                             @RequestParam @Positive @Max(5000) int height) {
        return new ByteArrayResource(chartaService.getSubCharta(id, x, y, width, height));
    }

    @DeleteMapping("{id}")
    public void deleteImage(@PathVariable long id) {
        chartaService.deleteImage(id);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
