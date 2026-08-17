package com.dlsu.medflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot edition of MedFlow.
 *
 * <p>Replaces {@code Launcher.java} / {@code Main.java} from the JavaFX
 * edition, whose only job was working around a JavaFX runtime quirk before
 * calling {@code Application.launch}. A Spring Boot app has no equivalent
 * problem - this class just boots the embedded web server.</p>
 */
@SpringBootApplication
public class MedFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedFlowApplication.class, args);
	}
}
