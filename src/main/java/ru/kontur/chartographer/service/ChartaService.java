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
import ru.kontur.chartographer.util.ChartaUtil;

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
        Charta chartaFromDb = chartaRepository.findById(id).orElseThrow(
                () -> new ChartaNotFoundException("Charta with id " + id + " is not found"));

        validateCoordinates(chartaFromDb, x, y, width, height);

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = resultImage.createGraphics();

        int currentHeight = 0;

        List<Block> sortedBlocks = new ArrayList<>(chartaFromDb.getBlocks());
        sortedBlocks.sort(Comparator.comparing(Block::getNumber));

        for (Block block : sortedBlocks) {

            if (isGoodBlock(y, height, block)) {

                ChartaUtil.ChartaSize chartaSize = ChartaUtil.calcSizeForGettingSubChart(block, x, y, width, height, currentHeight);

                BufferedImage image = processBlock(block, chartaSize);
                graphic.drawImage(image, 0, currentHeight, null);
                currentHeight += image.getHeight();
            }
        }
        graphic.dispose();
        return getBytes(resultImage);
    }


    public void updateCharta(long id, int x, int y, int width, int height, MultipartFile image) {

        Charta chartaFromDb = chartaRepository.findById(id).orElseThrow(
                () -> new ChartaNotFoundException("Charta with id " + id + " is not found"));

        validateCoordinates(chartaFromDb, x, y, width, height);

        int currentHeight = 0;

        // Надеюсь, что влезет в память
        BufferedImage newImage;
        try {
            newImage = ImageIO.read(new ByteArrayInputStream(image.getBytes()));
        } catch (IOException e) {
            throw new ChartaUploadingException("Charta during processing uploaded charta: " + e.getMessage());
        }

        List<Block> sortedBlocks = new ArrayList<>(chartaFromDb.getBlocks());
        sortedBlocks.sort(Comparator.comparing(Block::getNumber));

        for (Block block : sortedBlocks) {

            if (isGoodBlock(y, height, block)) {

                BufferedImage blockFromDb = fileSystemRepository.findInFileSystem(block.getLocation());

                ChartaUtil.ChartaSize chartaSize = ChartaUtil.calcSizeForGettingSubChart(block, x, y, width, height, currentHeight);

                for (int j = 0; j < chartaSize.getNew_width(); j++) {
                    for (int k = 0; k < chartaSize.getNew_height(); k++) {
                        blockFromDb.setRGB(chartaSize.getX() + j, chartaSize.getY() + k, newImage.getRGB(j + chartaSize.getX_shift(),
                                currentHeight + k + chartaSize.getY_shift()));
                    }
                }
                currentHeight += height;
                fileSystemRepository.updateBlock(blockFromDb, block);
            }
        }
    }

    public void deleteImage(long id) {
        if (!chartaRepository.existsById(id)) {
            throw new ChartaNotFoundException("Charta with id " + id + " is not found");
        }
        fileSystemRepository.deleteCharta(id);
        chartaRepository.deleteById(id);
    }

    private boolean isGoodBlock(int y, int height, Block block) {
        return y <= block.getEndOfBlock() && y >= block.getStartOfBlock() ||
                y + height <= block.getEndOfBlock() && y + height >= block.getStartOfBlock() ||
                y + height >= block.getEndOfBlock() && y <= block.getStartOfBlock();
    }


    private BufferedImage processBlock(Block block, ChartaUtil.ChartaSize chartaSize) {
        BufferedImage image = fileSystemRepository.findInFileSystem(block.getLocation());
        BufferedImage newImg = new BufferedImage(chartaSize.getWidth(), chartaSize.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = newImg.createGraphics();
        g2d.drawImage(image.getSubimage(chartaSize.getX(), chartaSize.getY(),
                        chartaSize.getNew_width(), chartaSize.getNew_height()),
                chartaSize.getX_shift(), chartaSize.getY_shift(), null);
        g2d.dispose();
        return newImg;
    }

    private List<Block> generateEmptyBlocks(int width, int height, long chartaId) {

        List<Block> blocks = Collections.synchronizedList(new ArrayList<>());
        int numberOfFullBlocks = height / 5000;
        int x = height - 5000 * numberOfFullBlocks != 0 ? 1 : 0;
        IntStream.range(0, numberOfFullBlocks + x).parallel().forEach(i -> {
            blocks.add(createBlock(
                    chartaId, i, width, x == 0 ? 5000 : (i == numberOfFullBlocks + x - 1 ? height - 5000 * numberOfFullBlocks : 5000)));
        });

        return blocks;
    }

    private Block createBlock(long chartaId, int blockNumber, int width, int height) {
        String location = fileSystemRepository.createBlock(chartaId, blockNumber, width, height);
        return blockRepository.save(new Block(blockNumber, location, width, height, 5000 * blockNumber, 5000 * (blockNumber + 1) - 1));
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