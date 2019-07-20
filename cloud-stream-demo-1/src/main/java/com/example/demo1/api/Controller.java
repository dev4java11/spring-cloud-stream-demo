package com.example.demo1.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo1.config.Channel;
import com.example.demo1.events.CreateBookEvt;
import com.example.demo1.events.CreateBookForErrorEvt;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class Controller {
	
	private Channel channels;

	@PostMapping("/create")
	public ResponseEntity<?> send(@RequestBody CreateBookEvt evt){
		channels.bookCreationOutput().send(MessageBuilder.withPayload(evt)
				.setHeader("type", CreateBookEvt.class.getSimpleName())
				.build());
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}
	
	@PostMapping("/create-error")
	public ResponseEntity<?> send(@RequestBody CreateBookForErrorEvt evt){
		channels.bookCreationOutput().send(MessageBuilder.withPayload(evt)
				.setHeader("type", CreateBookForErrorEvt.class.getSimpleName())
				.build());
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}
}
