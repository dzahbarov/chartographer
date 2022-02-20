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

    @Test()
    void testSubChartaInvalidCoordinates() {
        testCoordinates(150, 0, 1, 2);
    }

    @Test()
    void testSubChartaInvalidCoordinates2() {
        testCoordinates(-150, 0, 150, 2);
    }

    @Test()
    void testSubChartaInvalidCoordinates3() {
        testCoordinates(0, 350, 150, 2);
    }

    @Test()
    void testSubChartaInvalidCoordinates4() {
        testCoordinates( 0, -350, 150, 350);
    }

    @Test()
    void testSubChartaInvalidId() {
        Mockito.doReturn(Optional.empty()).when(chartaRepository).findById(ArgumentMatchers.any());
        ChartaNotFoundException exception = assertThrows(ChartaNotFoundException.class,
                () -> chartaService.getSubCharta(42L, 0, -350, 150, 350));
        assertEquals("Charta with id 42 is not found", exception.getMessage());
    }


    @Test()
    void testSubCharta1FullImage() throws IOException {
        runTestCat(1, 0, 0, 320, 320);
    }

    @Test()
    void testSubChart1PartInnerImage() throws IOException {
        runTestCat(2, 0, 0, 320, 100);
    }

    @Test()
    void testSubChart2PartInnerImage() throws IOException {
        runTestCat(3, 0, 0, 100, 320);

    }

    @Test()
    void testSubChart3PartInnerImage() throws IOException {
        runTestCat(4, 210, 0, 110, 320);
    }

    @Test()
    void testSubChart5() throws IOException {
        runTestCat(5, 0, 203, 320, 117);
    }

    @Test()
    void testSubChart6() throws IOException {
        runTestCat(6, 59, 59, 261, 261);
    }

    @Test()
    void testSubChart7() throws IOException {
        runTestCat(7, 27, 28, 205, 251);
    }

    @Test()
    void testSubChart8() throws IOException {
        runTestCat(8, -157, 23, 264, 258);
    }

    @Test()
    void testSubChart9() throws IOException {
        runTestCat(9, 86, -92, 218, 256);

    }

    @Test()
    void testSubChart10() throws IOException {
        runTestCat(10, 85, 39, 291, 254);
    }

    @Test()
    void testSubChart11() throws IOException {
        runTestCat(11, 49, 148, 247, 295);
    }


    @Test()
    void testSubChart12() throws IOException {
        runTestCat(12, -101, -63, 251, 353);
    }

    @Test()
    void testSubChart13() throws IOException {
        runTestCat(13, 79, -26, 365, 286);
    }

    @Test()
    void testSubChart14() throws IOException {
        runTestCat(14, 106, 87, 260, 334);
    }

    @Test()
    void testSubChart15() throws IOException {
        runTestCat(15, -74, 147, 361, 220);
    }

    @Test()
    void testSubChart16() throws IOException {
        runTestCat(16, -96, -44, 533, 455);
    }

    @Test()
    void testSubChart17() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(17, block1, block2, 400, 6000, -47, 4839, 260, 276);
    }

    @Test()
    void testSubChart17_2() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(17, block2, block1, 400, 6000, -47, 4839, 260, 276);
    }

    @Test()
    void testSubChart18() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(18, block1, block2, 400, 6000, -24, 4839, 449, 227);
    }

    @Test()
    void testSubChart18_2() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(18, block2, block1, 400, 6000, -24, 4839, 449, 227);
    }

    @Test()
    void testSubChart19() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(19, block1, block2, 400, 6000, 115, 4871, 360, 385);
    }

    @Test()
    void testSubChart19_2() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(19, block2, block1, 400, 6000, 115, 4871, 360, 385);
    }

    @Test()
    void testSubChart20() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(20, block1, block2, 400, 6000, -65, 5071, 565, 332);
    }

    @Test()
    void testSubChart20_2() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(20, block2, block1, 400, 6000, -65, 5071, 565, 332);
    }


    @Test()
    void testSubChart21() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(21, block1, block2, 400, 6000, -26, 4946, 448, 468);
    }

    @Test()
    void testSubChart21_2() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(21, block2, block1, 400, 6000, -26, 4946, 448, 468);
    }

    @Test()
    void testSubChart22() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(22, block1, block2, 400, 6000, -378, 4595, 1152, 1458);
    }

    @Test()
    void testSubChart22_2() throws IOException {
        Block block1 = new Block(0, 400, 5000, 0, 4999);
        Block block2 = new Block(1, 400, 1000, 5000, 5999);
        runTestDouble(22, block2, block1, 400, 6000, -378, 4595, 1152, 1458);
    }



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


    private void checkImagesEquals(BufferedImage newImage, BufferedImage outImage) {
        assertEquals(newImage.getHeight(), outImage.getHeight());
        assertEquals(newImage.getWidth(), outImage.getWidth());

        for (int i = 0; i < outImage.getWidth(); i++) {
            for (int j = 0; j < outImage.getHeight(); j++) {
                assertEquals(newImage.getRGB(i, j), outImage.getRGB(i, j));
            }
        }
    }

    private void runTestCat(int N, int x, int y, int width, int height) throws IOException {
        Block block = new Block(String.format("src/test/resources/testSubCharta%d/input.bmp", N), 320, 320, 0, 320);
        Charta charta = new Charta(42L, 320, 320);
        charta.setBlocks(List.of(block));

        Mockito.doReturn(Optional.of(charta))
                .when(chartaRepository)
                .findById(42L);

        Mockito.doReturn(ImageIO.read(new File(block.getLocation())))
                .when(fileSystemRepository)
                .findInFileSystem(block.getLocation());

        byte[] subCharta = chartaService.getSubCharta(42L, x, y, width, height);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File(String.format("src/test/resources/testSubCharta%d/expected_output.bmp", N)));
        checkImagesEquals(image, expectedImage);
    }

    private void runTestDouble(int N, Block block1, Block block2, int chartaWidth, int chartaHeight, int x, int y, int width, int height) throws IOException {
        if (block1.getNumber() == 0) {
            block1.setLocation(String.format("src/test/resources/testSubCharta%d/block1.bmp", N));
            block2.setLocation(String.format("src/test/resources/testSubCharta%d/block2.bmp", N));
        } else {
            block1.setLocation(String.format("src/test/resources/testSubCharta%d/block2.bmp", N));
            block2.setLocation(String.format("src/test/resources/testSubCharta%d/block1.bmp", N));
        }



        Charta charta = new Charta(42L, chartaWidth, chartaHeight);
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

        byte[] subCharta = chartaService.getSubCharta(42L, x, y, width, height);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(subCharta));
        BufferedImage expectedImage = ImageIO.read(new File(String.format("src/test/resources/testSubCharta%d/expected_output.bmp", N)));
        checkImagesEquals(image, expectedImage);
    }


    private void testCoordinates(int x, int y, int width, int height) {
        Mockito.doReturn(Optional.of(new Charta(42L, 150, 350))).when(chartaRepository).findById(ArgumentMatchers.any());

        InvalidChartaCoordinatesException exception = assertThrows(InvalidChartaCoordinatesException.class,
                () -> chartaService.getSubCharta(42L, x, y, width, height));

        assertEquals("Invalid coordinates", exception.getMessage());
    }

}