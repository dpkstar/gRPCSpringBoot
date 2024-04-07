package com.optum.comlayer;

import com.optum.comlayer.service.ComLayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ComLayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComLayerApplication.class, args);
	}

}

@Component
class SendJsonToOnCloudRunner implements ApplicationRunner {

	private final ComLayerService comLayerService;

	@Autowired
	public SendJsonToOnCloudRunner(ComLayerService comLayerService) {
		this.comLayerService = comLayerService;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// Call the method from ComLayerService
		comLayerService.sendJsonToOnCloud()
				.doOnSuccess(result -> System.out.println("Received response from OnCloud: " + result))
				.doOnError(error -> System.err.println("Error occurred: " + error))
				.doFinally(signal -> System.out.println("Processing completed"))
				.subscribe();
	}
}
