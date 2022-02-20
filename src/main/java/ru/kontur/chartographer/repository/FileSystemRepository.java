package ru.kontur.chartographer.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.kontur.chartographer.domain.Block;
import ru.kontur.chartographer.exception.ChartaCreatingException;
import ru.kontur.chartographer.exception.ChartaUpdatingException;
import ru.kontur.chartographer.exception.RenderImageException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * @author dzahbarov
 */

@Repository
public class FileSystemRepository {

    @Value("${content.folder}")
    private String str;

    private String RESOURCES_DIR = System.getProperty("user.dir");

    public BufferedImage findInFileSystem(String location) {
        try {
            return ImageIO.read(new File(location));
        } catch (IOException e) {
            throw new RenderImageException("Can't load image from hard drive: " + e.getMessage());
        }
    }

    public String createBlock(long chartaId, long blockId, int width, int height) {
        String location =  RESOURCES_DIR + str + chartaId + '/' + new Date().getTime() + "-" + Thread.currentThread().getName() + '-' + blockId;
        try {
            if (Path.of(location).getParent() != null) {
                Files.createDirectories(Path.of(location).getParent());
            }

            ImageIO.write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), "bmp",  new File(location));
        } catch (IOException e) {
            throw new ChartaCreatingException("Error during creating exception: " + e.getMessage());
        }
        return location;
    }

    public void updateBlock(BufferedImage blockFromDb, Block block) {
        try {
            ImageIO.write(blockFromDb, "bmp", new File(block.getLocation()));
        } catch (IOException e) {
            throw new ChartaUpdatingException("Exception during charta updating: " + e.getMessage());
        }
    }
}