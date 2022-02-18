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

        // может выезжать за границы

        BufferedImage concatImage = new BufferedImage(width,  height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = concatImage.createGraphics();
        int currentHeight = 0;
        for (int i = 0; i < chartaFromDb.getBlocks().size(); i++) {
            int startBlock = 5000 * i;
            int endBlock = 5000 * (i + 1)-1;

            if (y <= endBlock && y >= startBlock || y + height <= endBlock && y + height >= startBlock || y + height >= endBlock && y <= startBlock) {

                int yStart = Math.max(y-startBlock, 0);
                int newHeight = Math.min(endBlock - y, y + height - startBlock);
                //
                BufferedImage image = processBlock(chartaFromDb.getBlocks().get(i), x, yStart, width, newHeight);

                g2d.drawImage(image, 0, currentHeight, null);
                currentHeight += image.getHeight();
            }
        }

        g2d.dispose();

        return getBytes(concatImage);
    }

    private BufferedImage processBlock(Block block, int x, int y, int width, int height) {
        BufferedImage image = fileSystemRepository.findInFileSystem(block.getLocation());
        BufferedImage newImg = new BufferedImage(width,  height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = newImg.createGraphics();
        g2d.drawImage(image.getSubimage(Math.max(x, 0), y, image.getWidth()+x, Math.min(image.getHeight(), image.getHeight() - y)), x, y, null);
        g2d.dispose();
        return newImg;

    }


    public void updateCharta(long id, int x, int y, int width, int height, MultipartFile image) throws IOException {

        // Тут плохо
        Charta chartaFromDb = chartaRepository.findById(id)
                .orElseThrow(() -> new ChartaNotFoundException("Charta with id " + id + " is not found"));

        validateCoordinates(chartaFromDb, x, y, width, height);

        InputStream newImageIs = new ByteArrayInputStream(image.getBytes());
        int currentHeight = 0;
        int prev = 0;
        BufferedImage newImage = ImageIO.read(newImageIs);
        for (int i = 0; i < chartaFromDb.getBlocks().size(); i++) {
            int startBlock = 5000 * i;
            int endBlock = 5000 * (i + 1)-1;

            if (y <= endBlock && y >= startBlock || y + height <= endBlock && y + height >= startBlock || y + height >= endBlock && y <= startBlock) {
                int yStart = Math.max(y-startBlock, 0);
                int newHeight = Math.min(endBlock - y, y + height - startBlock);

                BufferedImage blImage = fileSystemRepository.findInFileSystem(chartaFromDb.getBlocks().get(i).getLocation());

                for (int j = 0; j < width; j++) {
                    for (int k = 0; k < newHeight; k++) {
                        blImage.setRGB(x + j, yStart + k, newImage.getRGB(j, k+prev));
                    }
                }
                prev = newHeight;

                // block save
                ImageIO.write(blImage, "bmp", new File(chartaFromDb.getBlocks().get(i).getLocation()));
//                newImage.getRGB();
//                BufferedImage image = processBlock(chartaFromDb.getBlocks().get(i), x, yStart, width, newHeight);

              ;
//                currentHeight += image.getHeight();
            }
        }




//        InputStream is = new ByteArrayInputStream(chartaFromDb.getImage());
//
//        try {
//            InputStream newImageIs = new ByteArrayInputStream(image.getBytes());
//
//            BufferedImage imageFromDb = ImageIO.read(is);
//            BufferedImage newImage = ImageIO.read(newImageIs);
//
//            for (int i = y; i < x + height; i++) {
//                for (int j = x; j < y + width; j++) {
//                    imageFromDb.setRGB(i, j, newImage.getRGB(i - y, j - x));
//                }
//            }
//
////            chartaFromDb.setImage(getBytes(imageFromDb));
//            chartaRepository.save(chartaFromDb);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
