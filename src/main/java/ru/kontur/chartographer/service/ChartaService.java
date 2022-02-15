package ru.kontur.chartographer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kontur.chartographer.domain.Charta;
import ru.kontur.chartographer.repository.ChartaRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author dzahbarov
 */


@Service
public class ChartaService {
    private final ChartaRepository chartaRepository;

    public ChartaService(ChartaRepository chartaRepository) {
        this.chartaRepository = chartaRepository;
    }


    public byte[] getPic(String id, int x, int y, int width, int height) {
        Long id_long = Long.parseLong(id);
        Charta chartaFromDb = chartaRepository.findById(id_long).orElse(null);
        InputStream is = new ByteArrayInputStream(chartaFromDb.getImage());
        try {
            BufferedImage imageFromDb = ImageIO.read(is);
            BufferedImage subimage = imageFromDb.getSubimage(x, y, width, height);
            return getBytes(subimage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public Long save(BufferedImage image) {
        byte[] bytes = getBytes(image);
        Charta charta = new Charta();
        charta.setImage(bytes);
        Charta chartaFromDb = chartaRepository.save(charta);
        return chartaFromDb.getId();
    }

    public void updateCharta(String id, int x, int y, int width, int height, MultipartFile image) {
        Charta chartaFromDb = chartaRepository.findById(Long.parseLong(id)).orElse(null);
        InputStream is = new ByteArrayInputStream(chartaFromDb.getImage());
//        BigBufferedImage image = new BigBufferedImage()
        try {
            InputStream newImageIs = new ByteArrayInputStream(image.getBytes());

            BufferedImage imageFromDb = ImageIO.read(is);
            BufferedImage newImage = ImageIO.read(newImageIs);

            for (int i = y; i < x + height; i++) {
                for (int j = x; j < y + width; j++) {;
                    imageFromDb.setRGB(i, j,  newImage.getRGB(i-y, j-x));
                }
            }

            chartaFromDb.setImage(getBytes(imageFromDb));
            chartaRepository.save(chartaFromDb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteImage(long id) {
        chartaRepository.deleteById(id);
    }

    private byte[] getBytes(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "bmp", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public String save(MultipartFile image) {
        Charta charta = new Charta();
        try {
            charta.setImage(image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.toString(chartaRepository.save(charta).getId());
    }

    public byte[] getPic(String id) {
        return chartaRepository.findById(Long.parseLong(id)).orElse(null).getImage();
    }
}
