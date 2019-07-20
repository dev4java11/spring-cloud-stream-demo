package com.example.demo1;


import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo1.config.Channel;
import com.example.demo1.events.CreateBookEvt;
import com.example.demo1.events.CreateBookForErrorEvt;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CloudStreamDemo1ApplicationTests {
	
	@Autowired
	private Channel channel;
	
	@Autowired
	private MessageCollector collector;

	@Test
	@Ignore
	public void test1() {
		MessageChannel output = channel.bookCreateOutput();
		log.debug("output: "+output);
		
		CreateBookEvt evt = new CreateBookEvt();
		evt.setTitle("Microservices");
		evt.setAutors(Arrays.asList(new CreateBookEvt.Autor("Martin Fowler")));
		
		Message<?> request = MessageBuilder.withPayload(evt).setHeader("type", CreateBookEvt.class.getSimpleName()).build();
		channel.bookCreateInput().send(request);
		
		Message<?> response = collector.forChannel(channel.bookCreateOutput()).poll();
		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getPayload());
		
		log.debug("Payload received: " + response.getPayload().toString());
	}

	@Test
	@Ignore
	public void test2() {
		CreateBookForErrorEvt evt = new CreateBookForErrorEvt();
		log.debug("Send create book for error event");
		
		Message<?> request = MessageBuilder.withPayload(evt).setHeader("type", CreateBookForErrorEvt.class.getSimpleName()).build();
		channel.bookCreateInput().send(request);
		
		Message<?> response = collector.forChannel(channel.bookCreateOutput()).poll();
		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getPayload());
		
		log.debug("Payload received: " + response.getPayload().toString());
	}
}
