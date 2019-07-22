package com.example.demo2;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.example.demo2.phrase.Channels;
import com.example.demo2.phrase.Phrase;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableBinding(Channels.class)
@Slf4j
public class CloudStreamDemo2Application {

	public static void main(String[] args) {
		SpringApplication.run(CloudStreamDemo2Application.class, args);
	}

	@Component
	@AllArgsConstructor(onConstructor_ = @Autowired)
	public class SendPrhasesApp implements ApplicationRunner {
		
		private Channels channels;
		
		@Override
		public void run(ApplicationArguments args) throws Exception {
			List<String> phrases = Arrays.asList("Welcome to Spring Framework", "Spring's Rock", "Beautifull Spring Boot App", "From ground to cloud");
			List<String> users = Arrays.asList("Josh Long", "Dave Syer", "Gary Russell", "Rob Winch" , "Craig Walls");
			
			Runnable command = () -> {
				int rndPhrase = new Random().nextInt(phrases.size());
				int rndUser = new Random().nextInt(users.size());
				
				Phrase phrase = new Phrase(String.valueOf(rndPhrase), phrases.get(rndPhrase), String.valueOf(rndUser), users.get(rndUser));
				Message<?> message = MessageBuilder
						.withPayload(phrase)
						.setHeader(KafkaHeaders.MESSAGE_KEY, phrase.getId().getBytes())
						.setHeader("idPhrase", phrase.getId())
						.setHeader("type", Phrase.class.getSimpleName())
						.build();
				log.debug("Send message " + message.toString());
				try {
					channels.phraseOutput().send(message);
				}catch(Exception e) {
					log.error(e.getMessage());
				}
			};
			
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(command, 0, 15, TimeUnit.SECONDS);
		}
	}
}
