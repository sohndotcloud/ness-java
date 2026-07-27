package sohn.cloud.ness_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NessBackendApplication {

	public static void main(String[] args) {
		System.setProperty("java.security.egd", "file:/dev/./urandom");
		SpringApplication.run(NessBackendApplication.class, args);
	}

}
