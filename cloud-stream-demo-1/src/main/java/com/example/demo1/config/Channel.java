package com.example.demo1.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Channel {

	public static final String BOOK_CREATE_INPUT = "bookCreateInput";
	public static final String BOOK_CREATE_OUTPUT = "bookCreateOutput";
	public static final String BOOK_CREATION_OUTPUT = "bookCreationOutput";
	
	@Input(BOOK_CREATE_INPUT)
	SubscribableChannel bookCreateInput();
	
	@Output(BOOK_CREATE_OUTPUT)
	MessageChannel bookCreateOutput();
	
	@Output(BOOK_CREATION_OUTPUT)
	MessageChannel bookCreationOutput();
}
