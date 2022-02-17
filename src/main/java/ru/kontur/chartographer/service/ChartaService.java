package ru.kontur.chartographer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kontur.chartographer.domain.Charta;
import ru.kontur.chartographer.exception.ChartaNotFoundException;
import ru.kontur.chartographer.exception.InvalidChartaCoordinatesException;
import ru.kontur.chartographer.exception.RenderImageException;
import ru.kontur.chartographer.repository.ChartaRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    public Charta createImage(int width, int height) {
        return chartaRepository.save(
                        new Charta(width, height, getBytes(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB))));
    }

    public byte[] getSubCharta(long id, int x, int y, int width, int height) {
        Charta chartaFromDb = chartaRepository.findById(id)
                .orElseThrow(() -> new ChartaNotFoundException("Charta with id " + id + " is not found"));

        validateCoordinates(chartaFromDb, x, y, width, height);

        // может выезжать за границы

        try (InputStream is = new ByteArrayInputStream(chartaFromDb.getImage())) {
            return getBytes(ImageIO.read(is).getSubimage(x, y, width, height));
        } catch (IOException e) {
            throw new RenderImageException("Can't render image");
        }
    }


    public void updateCharta(long id, int x, int y, int width, int height, MultipartFile image) {

        // Тут плохо
        Charta chartaFromDb = chartaRepository.findById(id)
                .orElseThrow(() -> new ChartaNotFoundException("Charta with id " + id + " is not found"));

        validateCoordinates(chartaFromDb, x, y, width, height);

        InputStream is = new ByteArrayInputStream(chartaFromDb.getImage());
//        BigBufferedImage image = new BigBufferedImage()
        try {
            InputStream newImageIs = new ByteArrayInputStream(image.getBytes());

            BufferedImage imageFromDb = ImageIO.read(is);
            BufferedImage newImage = ImageIO.read(newImageIs);

            for (int i = y; i < x + height; i++) {
                for (int j = x; j < y + width; j++) {
                    imageFromDb.setRGB(i, j, newImage.getRGB(i - y, j - x));
                }
            }

            chartaFromDb.setImage(getBytes(imageFromDb));
            chartaRepository.save(chartaFromDb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteImage(long id) {
        if (!chartaRepository.existsById(id)) {
            throw new ChartaNotFoundException("Charta with id " + id + " is not found");
        }
        chartaRepository.deleteById(id);
    }

    private byte[] getBytes(BufferedImage image) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "bmp", byteStream);
        } catch (IOException e) {
            throw new RenderImageException("Can't render image");
        }
        return byteStream.toByteArray();
    }

    private void validateCoordinates(Charta chartaFromDb, int x, int y, int width, int height) {
        if (x > chartaFromDb.getWidth() || y > chartaFromDb.getHeight() || x + width < 0 || y + height < 0) {
            throw new InvalidChartaCoordinatesException("Invalid coordinates for updating");
        }
    }

//    public String save(MultipartFile image) {
//        Charta charta = new Charta();
//        try {
//            charta.setImage(image.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return Long.toString(chartaRepository.save(charta).getId());
//    }

//    public byte[] getPic(long id) {
//        return chartaRepository.findById(id).orElse(null).getImage();
//    }
}
