package ru.kontur.chartographer.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.kontur.chartographer.domain.Block;
import ru.kontur.chartographer.domain.Charta;
import ru.kontur.chartographer.exception.ChartaNotFoundException;
import ru.kontur.chartographer.exception.InvalidChartaCoordinatesException;
import ru.kontur.chartographer.repository.BlockRepository;
import ru.kontur.chartographer.repository.ChartaRepository;
import ru.kontur.chartographer.repository.FileSystemRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dzahbarov
 */

@SpringBootTest
class ChartaServiceTest {

    @Autowired
    private ChartaService chartaService;

    @MockBean
    private ChartaRepository chartaRepository;

    @MockBean
    private FileSystemRepository fileSystemRepository;

    @MockBean
    private BlockRepository blockRepository;

    @Test
    void createSmallImage() {
        int width = 360;
        int height = 360;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 0, width, height);
        Mockito.verify(blockRepository, Mockito.times(1)).save(ArgumentMatchers.any());

        assertEquals(image.getBlocks().size(), 1);
    }

    @Test
    void create5000pxImage() {
        int width = 360;
        int height = 5000;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 0, width, height);
        Mockito.verify(blockRepository, Mockito.times(1)).save(ArgumentMatchers.any());

        assertEquals(image.getBlocks().size(), 1);
    }

    @Test
    void create5001pxImage() {
        int width = 360;
        int height = 5001;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 0L, width, 5000);
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 1L, width, 1);
        Mockito.verify(blockRepository, Mockito.times(2)).save(ArgumentMatchers.any());

        assertEquals(image.getBlocks().size(), 2);
    }

    @Test
    void create9999pxImage() {
        int width = 360;
        int height = 9999;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 0L, width, 5000);
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 1L, width, 4999);
        Mockito.verify(blockRepository, Mockito.times(2)).save(ArgumentMatchers.any());

        assertEquals(image.getBlocks().size(), 2);
    }

    @Test
    void create10000pxImage() {
        int width = 360;
        int height = 10000;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 0L, width, 5000);
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 1L, width, 5000);
        Mockito.verify(blockRepository, Mockito.times(2)).save(ArgumentMatchers.any());

        assertEquals(image.getBlocks().size(), 2);
    }

    @Test
    void create10001pxImage() {
        int width = 360;
        int height = 10001;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 0L, width, 5000);
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 1L, width, 5000);
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 2L, width, 1);

        Mockito.verify(blockRepository, Mockito.times(3)).save(ArgumentMatchers.any());

        assertEquals(image.getBlocks().size(), 3);
    }

    @Test
    void create45000pxImage() {
        int width = 360;
        int height = 45000;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        for (int i = 0; i < 9; i++) {
            Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, i, width, 5000);
        }
        Mockito.verify(blockRepository, Mockito.times(9)).save(ArgumentMatchers.any());
        assertEquals(image.getBlocks().size(), 9);
    }

    @Test
    void create45001pxImage() {
        int width = 360;
        int height = 45001;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        for (int i = 0; i < 9; i++) {
            Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, i, width, 5000);
        }
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 9, width, 1);
        Mockito.verify(blockRepository, Mockito.times(10)).save(ArgumentMatchers.any());
        assertEquals(image.getBlocks().size(), 10);
    }

    @Test
    void create46820pxImage() {
        int width = 360;
        int height = 46820;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        for (int i = 0; i < 9; i++) {
            Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, i, width, 5000);
        }
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 9, width, 1820);
        Mockito.verify(blockRepository, Mockito.times(10)).save(ArgumentMatchers.any());
        assertEquals(image.getBlocks().size(), 10);
    }

    @Test
    void create49999pxImage() {
        int width = 360;
        int height = 49_999;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        for (int i = 0; i < 9; i++) {
            Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, i, width, 5000);
        }
        Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, 9, width, 4_999);
        Mockito.verify(blockRepository, Mockito.times(10)).save(ArgumentMatchers.any());
        assertEquals(image.getBlocks().size(), 10);
    }

    @Test
    void create50000pxImage() {
        int width = 360;
        int height = 50_000;

        Mockito.doReturn(new Charta(42L, width, height))
                .when(chartaRepository)
                .save(ArgumentMatchers.any(Charta.class));

        Charta image = chartaService.createImage(width, height);

        Mockito.verify(chartaRepository, Mockito.times(2)).save(ArgumentMatchers.any());
        for (int i = 0; i < 10; i++) {
            Mockito.verify(fileSystemRepository, Mockito.times(1)).createBlock(42L, i, width, 5000);
        }
        Mockito.verify(blockRepository, Mockito.times(10)).save(ArgumentMatchers.any());
        assertEquals(image.getBlocks().size(), 10);
    }

    @Test()
    void testSubChartaInvalidCoordinates() {
        // картинка 150х350
        Mockito.doReturn(Optional.of(new Charta(42L, 150, 350)))
                .when(chartaRepository)
                .findById(ArgumentMatchers.any());

        InvalidChartaCoordinatesException exception = assertThrows(InvalidChartaCoordinatesException.class,
                () -> chartaService.getSubCharta(42L, 150, 0, 1, 2));

        String expect = "Invalid coordinates";
        assertEquals(expect, exception.getMessage());
    }

    @Test()
    void testSubChartaInvalidCoordinates2() {
        // картинка 150х350
        Mockito.doReturn(Optional.of(new Charta(42L, 150, 350)))
                .when(chartaRepository)
                .findById(ArgumentMatchers.any());

        InvalidChartaCoordinatesException exception = assertThrows(InvalidChartaCoordinatesException.class,
                () -> chartaService.getSubCharta(42L, -150, 0, 150, 2));

        String expect = "Invalid coordinates";
        assertEquals(expect, exception.getMessage());
    }

    @Test()
    void testSubChartaInvalidCoordinates3() {
        // картинка 150х350
        Mockito.doReturn(Optional.of(new Charta(42L, 150, 350)))
                .when(chartaRepository)
                .findById(ArgumentMatchers.any());

        InvalidChartaCoordinatesException exception = assertThrows(InvalidChartaCoordinatesException.class,
                () -> chartaService.getSubCharta(42L, 0, 350, 150, 2));

        String expect = "Invalid coordinates";
        assertEquals(expect, exception.getMessage());
    }

    @Test()
    void testSubChartaInvalidCoordinates4() {
        // картинка 150х350
        Mockito.doReturn(Optional.of(new Charta(42L, 150, 350)))
                .when(chartaRepository)
                .findById(ArgumentMatchers.any());

        InvalidChartaCoordinatesException exception = assertThrows(InvalidChartaCoordinatesException.class,
                () -> chartaService.getSubCharta(42L, 0, -350, 150, 350));

        String expect = "Invalid coordinates";
        assertEquals(expect, exception.getMessage());
    }

    @Test()
    void testSubChartaInvalidId() {
        // картинка 150х350
        Mockito.doReturn(Optional.empty())
                .when(chartaRepository)
                .findById(ArgumentMatchers.any());

        ChartaNotFoundException exception = assertThrows(ChartaNotFoundException.class,
                () -> chartaService.getSubCharta(42L, 0, -350, 150, 350));

        String expect = "Charta with id " + 42 + " is not found";
        assertEquals(expect, exception.getMessage());
    }


    @Test()
    void testSubCharta1FullImage() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta1/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 0, 0, 320, 320);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta1/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart1PartInnerImage() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta2/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 0, 0, 320, 100);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta2/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart2PartInnerImage() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta3/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 0, 0, 100, 320);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta3/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart3PartInnerImage() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta4/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 210, 0, 110, 320);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta4/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart5() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta5/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 0, 203, 320, 117);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta5/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart6() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta6/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 59, 59, 261, 261);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta6/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart7() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta7/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 27, 28, 205, 251);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta7/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart8() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta8/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, -157, 23, 264, 258);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta8/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart9() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta9/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 86, -92, 218, 256);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta9/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart10() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta10/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 85, 39, 291, 254);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta10/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart11() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta11/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 49, 148, 247, 295);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta11/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }


    @Test()
    void testSubChart12() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta12/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, -101, -63, 251, 353);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta12/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart13() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta13/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 79, -26, 365, 286);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta13/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart14() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta14/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, 106, 87, 260, 334);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta14/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart15() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta15/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, -74, 147, 361, 220);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta15/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart16() throws IOException {
        Block block = new Block("src/test/resources/testSubCharta16/input.bmp", 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, -96, -44, 533, 455);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta16/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
    }

    @Test()
    void testSubChart17() throws IOException {
        Block block1 = new Block("src/test/resources/testSubCharta17/block1.bmp", 400, 5000, 0, 4999);
        Block block2 = new Block("src/test/resources/testSubCharta17/block2.bmp", 400, 1000, 5000, 9999);

        Charta charta = new Charta(42L, 400, 6000);
        charta.setBlocks(List.of(block1, block2));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block1.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block1.getLocation());

        Mockito.doReturn(ImageIO.read(new File(block2.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block2.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, -47, 4839, 260, 276);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/testSubCharta17/expected_output.bmp"));
        checkImagesEquals(image, expectedImage);
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