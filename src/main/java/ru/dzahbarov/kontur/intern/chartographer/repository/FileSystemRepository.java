package ru.dzahbarov.kontur.intern.chartographer.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.dzahbarov.kontur.intern.chartographer.domain.Block;
import ru.dzahbarov.kontur.intern.chartographer.exception.ChartaDeletingException;
import ru.dzahbarov.kontur.intern.chartographer.exception.ChartaUpdatingException;
import ru.dzahbarov.kontur.intern.chartographer.exception.RenderImageException;
import ru.dzahbarov.kontur.intern.chartographer.exception.ChartaCreatingException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

/**
 * @author dzahbarov
 */

@Repository
public class FileSystemRepository {

    @Value("${content.folder}")
    private String RESOURCE_PATH;

//    private static final String PATH_TO_HOME = System.getProperty("user.dir");

    public BufferedImage findInFileSystem(String location) {
        try {
            return ImageIO.read(new File(location));
        } catch (IOException e) {
            throw new RenderImageException("Can't load image from hard drive: " + e.getMessage());
        }
    }

    public String createBlock(long chartaId, long blockId, int width, int height) {

        String location =  RESOURCE_PATH + "/" + chartaId + '/' + new Date().getTime() + "-" + Thread.currentThread().getName() + '-' + blockId;
        try {
            if (Path.of(location).getParent() != null) {
                Files.createDirectories(Path.of(location).getParent());
            }
            ImageIO.write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), "bmp", new File(location));
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

    public boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public void deleteCharta(long chartaId) {
        if (!deleteDirectory(new File(RESOURCE_PATH +"/"+ chartaId))) {
            throw new ChartaDeletingException("Can't delete charta block");
        }
    }

    public void makeTmp(BufferedImage blockFromDb, Block block) {
        try {
            ImageIO.write(blockFromDb, "bmp", new File(block.getLocation() + "tmp" + Thread.currentThread().getName()));
        } catch (IOException e) {
            throw new ChartaUpdatingException("Exception during charta updating: " + e.getMessage());
        }
    }

    public synchronized void save(List<Block> interestingBlocks) {
        for (Block block : interestingBlocks) {
            Path source = Path.of(block.getLocation() + "tmp" + Thread.currentThread().getName());
            Path target = Path.of(block.getLocation()).getFileName();
            try {
                Files.move(source, source.resolveSibling(target), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                throw new ChartaUpdatingException("Exception during charta updating: " + e.getMessage());
            }
        }
    }
}