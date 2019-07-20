package com.example.demo1.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.example.demo1.book.Book;
import com.example.demo1.config.Channel;
import com.example.demo1.events.BookCreatedEvt;
import com.example.demo1.events.CreateBookEvt;
import com.example.demo1.events.CreateBookForErrorEvt;
import com.example.demo1.events.ErrorCreateBookEvt;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@EnableBinding(Channel.class)
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookService {

	private Channel channels;

	@StreamListener(target = Channel.BOOK_CREATE_INPUT, condition = "headers['type']=='CreateBookEvt'")
	public void on(CreateBookEvt evt) {
		log.debug("Get Event: " + evt);
		Book book = new Book(evt);
		log.debug("Book created: " + book.toString());
		BookCreatedEvt evt_return = book.bookCreated();
		log.debug("Return Book Created Event: " + evt_return.toString());
		channels.bookCreateOutput()
				.send(MessageBuilder.withPayload(evt_return)
				.setHeader("idBook", evt_return.getId())
				.setHeader("type", BookCreatedEvt.class.getSimpleName()).build());
	}
	
	@StreamListener(target = Channel.BOOK_CREATE_INPUT, condition = "headers['type']=='CreateBookForErrorEvt'")
	public void on(CreateBookForErrorEvt evt) {
		if(evt.getError().booleanValue()) {
			log.debug("BOOOOOOOOOOOOOOOOOOOOOOOOOOM !!!!");
			throw new RuntimeException("BOOM, Error for create book.");
		}else {
			log.debug("Not Error");
		}
	}
	
	@StreamListener(target = Channel.BOOK_CREATE_INPUT, condition = "headers['type']=='ErrorCreateBookEvt'")
	public void on(ErrorCreateBookEvt evt) {
		log.debug("Get Event: " + evt);
		channels.bookCreateOutput()
			.send(MessageBuilder.withPayload(evt)
			.setHeader("idError", evt.getId())
			.setHeader("type", ErrorCreateBookEvt.class.getSimpleName())
			.build());
	}
	
	@ServiceActivator(inputChannel = "book.create.input.g1.errors")
	public void error(Message<?> message) {
		log.debug("Get message error: " + message.toString());
		ErrorCreateBookEvt evt = new ErrorCreateBookEvt(UUID.randomUUID().toString());
		channels.bookCreateInput()
				.send(MessageBuilder.withPayload(evt)
				.setHeader("idError", evt.getId())
				.setHeader("type", ErrorCreateBookEvt.class.getSimpleName())
				.build());
	}
	
//	@StreamListener("errorChannel")
//	public void error(Message<?> message) {
//		log.debug("Get message error: " + message.toString());
//		ErrorCreateBookEvt evt = new ErrorCreateBookEvt(UUID.randomUUID().toString());
//		channels.bookCreateInput()
//				.send(MessageBuilder.withPayload(evt)
//				.setHeader("idError", evt.getId())
//				.setHeader("type", ErrorCreateBookEvt.class.getSimpleName())
//				.build());
//	}
}
