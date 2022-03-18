package ru.dzahbarov.kontur.intern.chartographer.util;

import lombok.Getter;
import lombok.Setter;
import ru.dzahbarov.kontur.intern.chartographer.domain.Block;

/**
 * @author dzahbarov
 */
public class ChartaUtil {
    public static ChartaSize calcSizeForGettingSubChart(Block block, int x, int y, int width, int height, int currentHeight) {
        y = y - block.getStartOfBlock();

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


        return new ChartaSize(x, y, width, height, new_width, new_height, x_shift, y_shift);
    }

    @Getter
    @Setter
    public static class ChartaSize {
        private int x;
        private int y;
        private int width;
        private int height;
        private int new_width;
        private int new_height;
        private int x_shift;
        private int y_shift;


        public ChartaSize(int x, int y, int width, int height, int new_width, int new_height, int x_shift, int y_shift) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.new_width = new_width;
            this.new_height = new_height;
            this.x_shift = x_shift;
            this.y_shift = y_shift;
        }
    }

    private static int changeHeightForBorders(int y, Block block, int new_height) {
        if (y + new_height > block.getHeight()) {
            new_height = block.getHeight() - y;
        }
        return new_height;
    }

    private static int changeWidthForBorders(int x, Block block, int new_width) {
        if (x + new_width > block.getWidth()) {
            new_width = block.getWidth() - x;
        }
        return new_width;
    }

    private static int getWidth(int x, int width) {
        int new_width = width;
        if (x < 0) {
            new_width = width - Math.abs(x);
        }
        return new_width;
    }

    private static int getHeight(int y, int height) {
        int new_height = height;
        if (y < 0) {
            new_height = height - Math.abs(y);
        }
        return new_height;
    }

    private static int getShift(int var) {
        int var_shift = 0;
        if (var < 0) {
            var_shift = Math.abs(var);
        }
        return var_shift;
    }


}
