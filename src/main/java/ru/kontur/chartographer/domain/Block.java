package ru.kontur.chartographer.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author dzahbarov
 */

@Entity
@Getter
@Setter
public class Block {
    @Id
    @GeneratedValue()
    private Long id;

    private String location;

    private int width;

    private int height;
}
