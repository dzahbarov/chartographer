package ru.kontur.chartographer.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;


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

    @Lob
    private byte[] image;

    public Charta(int width, int height, byte[] image) {
        this.height = height;
        this.width = width;
        this.image = image;
    }

    public Charta() {

    }
}
