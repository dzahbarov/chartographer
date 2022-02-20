package ru.kontur.chartographer.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.util.List;


/**
 * @author dzahbarov
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Charta {

    @Id
    @GeneratedValue
    private Long id;

    @Positive
    @Max(50_000)
    private int height;

    @Positive
    @Max(20_000)
    private int width;

    @OneToMany()
    private List<Block> blocks;

    public Charta(int width, int height) {
        this.height = height;
        this.width = width;
    }

    public Charta(Long id, int width, int height) {
        this.id = id;
        this.height = height;
        this.width = width;
    }
}
