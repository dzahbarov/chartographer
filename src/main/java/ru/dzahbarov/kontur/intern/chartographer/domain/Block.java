package ru.dzahbarov.kontur.intern.chartographer.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author dzahbarov
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Block {
    @Id
    @GeneratedValue()
    private long id;

    private int number;

    private String location;

    private int width;

    private int height;

    private int startOfBlock;

    private int endOfBlock;

    public Block(String location, int width, int height, int start, int end) {
        this.location = location;
        this.width = width;
        this.height = height;
        this.startOfBlock = start;
        this.endOfBlock = end;
    }

    public Block(int number, String location, int width, int height, int start, int end) {
        this.number = number;
        this.location = location;
        this.width = width;
        this.height = height;
        this.startOfBlock = start;
        this.endOfBlock = end;
    }
    public Block(int number, int width, int height, int start, int end) {
        this.number = number;
        this.width = width;
        this.height = height;
        this.startOfBlock = start;
        this.endOfBlock = end;
    }

}
