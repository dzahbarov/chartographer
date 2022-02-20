package ru.kontur.chartographer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kontur.chartographer.domain.Block;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dzahbarov
 */

@SpringBootTest(properties = {"content.folder=/test/"})
class FileSystemRepositoryTest {

    @Autowired
    FileSystemRepository fileSystemRepository;

    @Test
    void createOneSimpleBlock() {
        String location = System.getProperty("user.dir") + "/test/";
        long chartaId = 2;
        long blockId = 4;
        int width = 320;
        int height = 320;

        fileSystemRepository.createBlock(chartaId, blockId, width, height);

        Assertions.assertTrue(Files.isDirectory(Path.of(location)));
        Assertions.assertTrue(Files.isDirectory(Path.of(location + chartaId)));
        Assertions.assertEquals(1, Objects.requireNonNull(new File(location + chartaId).list()).length);
    }

    @Test
    void createOneBigBlock() {
        String location = System.getProperty("user.dir") + "/test/";
        long chartaId = 3;
        long blockId = 6;
        int width = 20000;
        int height = 5000;

        fileSystemRepository.createBlock(chartaId, blockId, width, height);

        Assertions.assertTrue(Files.isDirectory(Path.of(location)));
        Assertions.assertTrue(Files.isDirectory(Path.of(location + chartaId)));
        Assertions.assertEquals(1, Objects.requireNonNull(new File(location + chartaId).list()).length);
    }


    @Test
    void updateBlock() throws IOException {
        BufferedImage newImage = ImageIO.read(new File("src/test/resources/testUpdateBlock/input.bmp"));
        ImageIO.write(new BufferedImage(newImage.getWidth(), newImage.getHeight(), BufferedImage.TYPE_INT_RGB), "bmp",
                new File("src/test/resources/testUpdateBlock/output.bmp"));
        Block block = new Block("src/test/resources/testUpdateBlock/output.bmp", newImage.getWidth(), newImage.getHeight(), 0, 359);

        fileSystemRepository.updateBlock(newImage, block);

        BufferedImage outImage = ImageIO.read(new File(block.getLocation()));

        checkImagesEquals(newImage, outImage);
    }



    @Test
    void findInFileSystem() throws IOException {
        String location = "src/test/resources/testFindInFileSystem/input.bmp";
        BufferedImage image1 = fileSystemRepository.findInFileSystem(location);
        BufferedImage image2 = ImageIO.read(new File(location));
        checkImagesEquals(image1, image2);
    }

    private void checkImagesEquals(BufferedImage newImage, BufferedImage outImage) {
        assertEquals(newImage.getHeight(), outImage.getHeight());
        assertEquals(newImage.getWidth(), outImage.getWidth());

        for (int i = 0; i < outImage.getWidth(); i++) {
            for (int j = 0; j < outImage.getHeight(); j++) {
                assertEquals(newImage.getRGB(i, j), outImage.getRGB(i, j));
            }
        }
    }
}