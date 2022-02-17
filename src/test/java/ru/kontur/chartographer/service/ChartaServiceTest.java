package ru.kontur.chartographer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;
import ru.kontur.chartographer.domain.Charta;
import ru.kontur.chartographer.repository.ChartaRepository;

/**
 * @author dzahbarov
 */

@SpringBootTest
class ChartaServiceTest {

    @Autowired
    private ChartaService chartaService;

    @MockBean
    private ChartaRepository chartaRepository;

    @Test
    void createImage() {
        int width = 100;
        int height = 1000;

//        Mockito.doReturn(new Charta(width, height, new byte[width * ]))

        Charta charta = chartaService.createImage(width, height);
        Assertions.assertEquals(width, charta.getWidth());
        Assertions.assertEquals(height, charta.getHeight());
        Assertions.assertArrayEquals(new byte[width*height*3], charta.getImage());
    }
}