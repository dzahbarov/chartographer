package ru.kontur.chartographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChartographerApplication {

	public static void main(String[] args) {
		System.setProperty("content.folder", args[0]);
		SpringApplication.run(ChartographerApplication.class, args);
	}

}
