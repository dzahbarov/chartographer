package ru.kontur.chartographer.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;


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

    @Lob
    private byte[] image;
}
