
package ru.kontur.chartographer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kontur.chartographer.domain.Block;
import ru.kontur.chartographer.domain.Charta;
import ru.kontur.chartographer.exception.ChartaNotFoundException;
import ru.kontur.chartographer.exception.InvalidChartaCoordinatesException;
import ru.kontur.chartographer.exception.RenderImageException;
import ru.kontur.chartographer.repository.BlockRepository;
import ru.kontur.chartographer.repository.ChartaRepository;
import ru.kontur.chartographer.repository.FileSystemRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;


/**
 * @author dzahbarov
 */

@Service
public class ChartaService {
    private final ChartaRepository chartaRepository;
    private final FileSystemRepository fileSystemRepository;
    private final BlockRepository blockRepository;

    public ChartaService(ChartaRepository chartaRepository, FileSystemRepository fileSystemRepository, BlockRepository blockRepository) {
        this.chartaRepository = chartaRepository;
        this.fileSystemRepository = fileSystemRepository;
        this.blockRepository = blockRepository;
    }

    public Charta createImage(int width, int height) throws IOException {
        Charta charta = chartaRepository.save(new Charta(width, height));
        List<Block> blocks = fileSystemRepository.generateEmptyBlocks(width, height, charta.getId());
        charta.setBlocks(blocks);
        return chartaRepository.save(charta);
    }

    public byte[] getSubCharta(long id, int x, int y, int width, int height) throws IOException {
        Charta chartaFromDb = chartaRepository.findById(id)
                .orElseThrow(() -> new ChartaNotFoundException("Charta with id " + id + " is not found"));

        validateCoordinates(chartaFromDb, x, y, width, height);

        BufferedImage concatImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = concatImage.createGraphics();
        int currentHeight = 0;
        int saved_x = x;
        int saved_y = y;
        int saved_height = height;

        for (int i = 0; i < chartaFromDb.getBlocks().size(); i++) {
            int startBlock = 5000 * i;
            int endBlock = 5000 * (i + 1) - 1;

            x = saved_x;
            y = saved_y;
            height = saved_height;
            if (y <= endBlock && y >= startBlock || y + height <= endBlock && y + height >= startBlock || y + height >= endBlock && y <= startBlock) {

                Block block = chartaFromDb.getBlocks().get(i);

                y = y - startBlock;

                // Если y отрицательный, то нужно поменять высоту
                // Если x отрицательный, то нужно поменять ширину

                // Посчитали сдвиг по x
                int x_shift = 0;
                if (x < 0) {
                    x_shift = Math.abs(x);
                }

                // Посчитали сдвиг по y
                int y_shift = 0;
                if (y < 0) {
                    y_shift = Math.abs(y);
                }

                // y - начало по высоте, y + height - конец по высоте

                // Случай, если y вылезает в отрицательные
                int new_height = height;
                if (y < 0) {
                    new_height = height - Math.abs(y);
                    y = 0;
                }

                // Случай, если x вылезает в отрицательные
                int new_width = width;
                if (x < 0) {
                    new_width = width - Math.abs(x);
                    x = 0;
                }

                // Пересчитаем ширину, чтобы она влезала в границы картинки
                if (x + new_width > block.getWidth()) {
                    new_width = block.getWidth() - x - 1;
                }

                // Пересчитаем высоту, чтобы она влезала в границы картинки
                if (y + new_height > block.getHeight()) {
                    new_height = block.getHeight() - y - 1;
                    height = block.getHeight() - y - 1;
                }

                // Теперь займемся блоками со склейкой
                // Нужно пересчитать высоту при склейке
                height -= currentHeight;

                if (currentHeight != 0) {
                    y_shift = 0;
                }

                BufferedImage image = processBlock(block, x, y, width, height, new_width, new_height, x_shift, y_shift);

                g2d.drawImage(image, 0, currentHeight, null);
                currentHeight += image.getHeight();
            }
        }

        g2d.dispose();

        return getBytes(concatImage);
    }

    private BufferedImage processBlock(Block block, int x, int y, int old_width, int old_height, int new_width, int new_height, int x_shift, int y_shift) {
        BufferedImage image = fileSystemRepository.findInFileSystem(block.getLocation());
        // Создали блок нужного размера
        // Но надо сверху высоту пересчитать
        BufferedImage newImg = new BufferedImage(old_width, old_height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = newImg.createGraphics();

        // Считаем, что x, y уже пересчитаны так, что он не отрицательные
        // Ширина и высота влезают в границы картинки
        g2d.drawImage(image.getSubimage(x, y, new_width, new_height), x_shift, y_shift, null);
        g2d.dispose();
        return newImg;

    }


    public void updateCharta(long id, int x, int y, int width, int height, MultipartFile image) throws IOException {

        Charta chartaFromDb = chartaRepository.findById(id)
                .orElseThrow(() -> new ChartaNotFoundException("Charta with id " + id + " is not found"));

        validateCoordinates(chartaFromDb, x, y, width, height);

        // Надеюсь что она влезет в память
        InputStream newImageIs = new ByteArrayInputStream(image.getBytes());

        int currentHeight = 0;

        int saved_y = y;
        int saved_height = height;
        BufferedImage newImage = ImageIO.read(newImageIs);
        for (int i = 0; i < chartaFromDb.getBlocks().size(); i++) {
            int startBlock = 5000 * i;
            int endBlock = 5000 * (i + 1) - 1;

            y = saved_y;
            height = saved_height;

            if (y <= endBlock && y >= startBlock || y + height <= endBlock && y + height >= startBlock || y + height >= endBlock && y <= startBlock) {

                BufferedImage blockFromDb = fileSystemRepository.findInFileSystem(chartaFromDb.getBlocks().get(i).getLocation());
                Block block = chartaFromDb.getBlocks().get(i);

                y = y - startBlock;

                int x_shift = 0;
                if (x < 0) {
                    x_shift = Math.abs(x);
                    width = width - x_shift;
                    x = 0;
                }

                int y_shift = 0;
                if (y < 0) {
                    y_shift = Math.abs(y);
                    height = height - y_shift;
                    y = 0;
                }

                if (x + width > block.getWidth()) {
                    width = block.getWidth() - x - 1;
                }

                if (y + height > block.getHeight()) {
                    height = block.getHeight() - y - 1;
                }

                if (currentHeight != 0) {
                    y_shift = 0;
                }

                for (int j = 0; j < width; j++) {
                    for (int k = 0; k < height; k++) {
                        blockFromDb.setRGB(x + j, y + k, newImage.getRGB(j + x_shift, currentHeight + k + y_shift));
                    }
                }

                currentHeight += height;
                ImageIO.write(blockFromDb, "bmp", new File(chartaFromDb.getBlocks().get(i).getLocation()));
            }
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
        if (x >= chartaFromDb.getWidth() || y >= chartaFromDb.getHeight() || x + width <= 0 || y + height <= 0) {
            throw new InvalidChartaCoordinatesException("Invalid coordinates for updating");
        }
    }
}