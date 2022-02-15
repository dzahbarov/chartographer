package ru.kontur.chartographer.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kontur.chartographer.service.ChartaService;

import java.awt.image.BufferedImage;

/**
 * @author dzahbarov
 */
@RestController
@RequestMapping("chartas")
public class ChartographerController {
    private final ChartaService chartaService;

    public ChartographerController(ChartaService chartaService) {
        this.chartaService = chartaService;
    }

    @PostMapping()
    public String createImage(@RequestParam int width, @RequestParam int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        return chartaService.save(image).toString();
    }

    @PostMapping(value = "{id}")
    public void updateImage(@PathVariable String id,
                               @RequestParam int x,
                               @RequestParam int y,
                               @RequestParam int width,
                               @RequestParam int height,
                            @RequestParam(value = "image", required = true) MultipartFile image) {
        chartaService.updateCharta(id, x, y, width, height, image);
    }


    @PostMapping("test")
    public String saveImage(@RequestParam(value = "image", required = true) MultipartFile image) {
        return chartaService.save(image).toString();
    }

    @GetMapping(value = "test/{id}", produces = "image/bmp")
    public Resource getImage(@PathVariable String id) {
        byte[] image = chartaService.getPic(id);

        return new ByteArrayResource(image);
    }

    @GetMapping(value = "{id}", produces = "image/bmp")
    public Resource getImage(@PathVariable String id,
                             @RequestParam int x,
                             @RequestParam int y,
                             @RequestParam int width,
                             @RequestParam int height) {
        byte[] image = chartaService.getPic(id, x, y, width, height);

        return new ByteArrayResource(image);
    }

    @DeleteMapping("{id}")
    public void deleteImage(@PathVariable String id){
        chartaService.deleteImage(Long.parseLong(id));
    }


}
