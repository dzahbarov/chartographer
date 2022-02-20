package ru.kontur.chartographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

@SpringBootApplication
public class ChartographerApplication {

    public static void main(String[] args) {
        if (args == null || args.length != 1 || args[0] == null) {
            throw new IllegalArgumentException("Required one argument: path to the content folder");
        }

        try {
            Path.of(args[0]);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal path to the content folder");
        }

        System.setProperty("content.folder", args[0]);
        SpringApplication.run(ChartographerApplication.class, args);
    }
}
