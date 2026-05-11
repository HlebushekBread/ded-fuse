package net.softloaf.ded_fuse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DedFuseApplication {

	public static void main(String[] args) {
		SpringApplication.run(DedFuseApplication.class, args);
	}

}
