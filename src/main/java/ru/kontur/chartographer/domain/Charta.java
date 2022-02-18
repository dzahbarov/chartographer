package ru.kontur.chartographer.domain;

import lombok.Builder;
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
public class Charta {

    @Id
    @GeneratedValue
    private long id;

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

    public Charta() {

    }
}
