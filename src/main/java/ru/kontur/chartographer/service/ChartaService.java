package ru.kontur.chartographer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.kontur.chartographer.domain.Block;
import ru.kontur.chartographer.domain.Charta;
import ru.kontur.chartographer.exception.ChartaNotFoundException;
import ru.kontur.chartographer.exception.ChartaUploadingException;
import ru.kontur.chartographer.exception.InvalidChartaCoordinatesException;
import ru.kontur.chartographer.exception.RenderImageException;
import ru.kontur.chartographer.repository.BlockRepository;
import ru.kontur.chartographer.repository.ChartaRepository;
import ru.kontur.chartographer.repository.FileSystemRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;


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

    public Charta createImage(int width, int height) {
        Charta charta = chartaRepository.save(new Charta(width, height));
        charta.setBlocks(generateEmptyBlocks(width, height, charta.getId()));
        chartaRepository.save(charta);
        return charta;
    }

    public byte[] getSubCharta(long id, int x, int y, int width, int height) {
        Charta chartaFromDb = chartaRepository.findById(id).orElseThrow(() -> new ChartaNotFoundException("Charta with id " + id + " is not found"));

        validateCoordinates(chartaFromDb, x, y, width, height);

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = resultImage.createGraphics();

        int currentHeight = 0;

        int saved_x = x;
        int saved_y = y;
        int saved_height = height;
//        List<Block> sorted = Collections.sort(chartaFromDb.getBlocks(), Comparator.comparing(Block::getId));
//                chartaFromDb.getBlocks().sort());

        List<Block> sortedBlocks = new ArrayList<>(chartaFromDb.getBlocks());
        sortedBlocks.sort(Comparator.comparing(Block::getId));
        for (Block block : sortedBlocks) {

            int startBlock = block.getStartOfBlock();
            int endBlock = block.getEndOfBlock();

            x = saved_x;
            y = saved_y;
            height = saved_height;

            if (isGoodBlock(y, endBlock, startBlock, height)) {

                y = y - startBlock;

                // Посчитали сдвиг по x и y
                int x_shift = getShift(x);
                int y_shift = getShift(y);

                // Случай, если x или y вылезает в отрицательные
                int new_height = getHeight(y, height);
                int new_width = getWidth(x, width);
                y = Math.max(y, 0);
                x = Math.max(x, 0);

                // Пересчитаем ширину и высоту, чтобы они влезали в границы картинки
                new_width = changeWidthForBorders(x, block, new_width);
                new_height = changeHeightForBorders(y, block, new_height);

//                height = changeHeightForBorders(y, block, new_height);

                if (y + new_height >= 5000) {
                    height = block.getHeight() - y;
                }

                // Нужно пересчитать высоту при склейке
                height -= currentHeight;
                if (currentHeight != 0) y_shift = 0;

                BufferedImage image = processBlock(block, x, y, width, height, new_width, new_height, x_shift, y_shift);
                graphic.drawImage(image, 0, currentHeight, null);
                currentHeight += image.getHeight();
            }
        }
        graphic.dispose();
        return getBytes(resultImage);
    }

    public void updateCharta(long id, int x, int y, int width, int height, MultipartFile image) {

        Charta chartaFromDb = chartaRepository.findById(id).orElseThrow(() -> new ChartaNotFoundException("Charta with id " + id + " is not found"));

        validateCoordinates(chartaFromDb, x, y, width, height);

        int currentHeight = 0;

        int saved_y = y;
        int saved_height = height;

        // Надеюсь, что влезет в память
        BufferedImage newImage;
        try {
            newImage = ImageIO.read(new ByteArrayInputStream(image.getBytes()));
        } catch (IOException e) {
            throw new ChartaUploadingException("Charta during processing uploaded charta: " + e.getMessage());
        }

        for (Block block : chartaFromDb.getBlocks()) {
            int startBlock = block.getStartOfBlock();
            int endBlock = block.getEndOfBlock();

            y = saved_y;
            height = saved_height;

            if (isGoodBlock(y, endBlock, startBlock, height)) {

                BufferedImage blockFromDb = fileSystemRepository.findInFileSystem(block.getLocation());

                y = y - startBlock;

                int x_shift = getShift(x);
                int y_shift = getShift(y);

                width = getWidth(x, width);
                x = Math.max(x, 0);

                height = getHeight(y, height);
                y = Math.max(y, 0);

                width = changeWidthForBorders(x, block, width);
                height = changeHeightForBorders(y, block, height);



                if (currentHeight != 0) y_shift = 0;

                for (int j = 0; j < width; j++) {
                    for (int k = 0; k < height; k++) {
                        blockFromDb.setRGB(x + j, y + k, newImage.getRGB(j + x_shift, currentHeight + k + y_shift));
                    }
                }
                currentHeight += height;
                fileSystemRepository.updateBlock(blockFromDb, block);
            }
        }
    }

    // TODO добавить удаление
    public void deleteImage(long id) {
        if (!chartaRepository.existsById(id)) {
            throw new ChartaNotFoundException("Charta with id " + id + " is not found");
        }

        chartaRepository.deleteById(id);
    }


    private int changeHeightForBorders(int y, Block block, int new_height) {
        if (y + new_height > block.getHeight()) {
            new_height = block.getHeight() - y;
        }
        return new_height;
    }

    private int changeWidthForBorders(int x, Block block, int new_width) {
        if (x + new_width > block.getWidth()) {
            new_width = block.getWidth() - x;
        }
        return new_width;
    }

    private int getWidth(int x, int width) {
        int new_width = width;
        if (x < 0) {
            new_width = width - Math.abs(x);
        }
        return new_width;
    }

    private int getHeight(int y, int height) {
        int new_height = height;
        if (y < 0) {
            new_height = height - Math.abs(y);
        }
        return new_height;
    }

    private int getShift(int var) {
        int var_shift = 0;
        if (var < 0) {
            var_shift = Math.abs(var);
        }
        return var_shift;
    }

    private boolean isGoodBlock(int y, int endBlock, int startBlock, int height) {
        return y <= endBlock && y >= startBlock || y + height <= endBlock && y + height >= startBlock || y + height >= endBlock && y <= startBlock;
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

    private List<Block> generateEmptyBlocks(int width, int height, long chartaId) {

        List<Block> blocks = Collections.synchronizedList(new ArrayList<>());
        int numberOfFullBlocks = height / 5000;
        int x = height - 5000 * numberOfFullBlocks != 0 ? 1 : 0;
        IntStream.range(0, numberOfFullBlocks + x).parallel().forEach(i -> {
            blocks.add(createBlock(chartaId, i, width, x == 0 ? 5000 : (i == numberOfFullBlocks + x - 1 ? height - 5000 * numberOfFullBlocks : 5000)));
        });

        return blocks;
    }

    private Block createBlock(long chartaId, int blockId, int width, int height) {
        String location = fileSystemRepository.createBlock(chartaId, blockId, width, height);
        return blockRepository.save(new Block(blockId, location, width, height, 5000 * blockId, 5000 * (blockId + 1) - 1));
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
            throw new InvalidChartaCoordinatesException("Invalid coordinates");
        }
    }
}