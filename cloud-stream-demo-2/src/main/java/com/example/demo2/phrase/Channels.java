package com.example.demo2.phrase;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Channels {

	public static final String PHRASE_OUTPUT = "phraseOutput";
	
	public static final String PHRASE_INPUT = "phraseInput";
	
	public static final String PHRASE_STREAMING_INPUT = "phraseStreamingInput";
	
	public static final String TOTAL_PHRASE_STREAMING_OUTPUT = "totalPhraseStreamingOutput";
	
	public static final String TOTAL_PHRASE_STREAMING_INPUT = "totalPhraseStreamingIntput";

	@Output(PHRASE_OUTPUT)
	MessageChannel phraseOutput();
	
	@Input(PHRASE_INPUT)
	SubscribableChannel phraseInput();
	
	@Input(PHRASE_STREAMING_INPUT)
	KStream<?, ?> phraseStreamingInput();
	
	@Output(TOTAL_PHRASE_STREAMING_OUTPUT)
	KStream<?, ?> totalPhraseStreamingOutput();
	
	@Input(TOTAL_PHRASE_STREAMING_INPUT)
	KTable<?, ?> totalPhraseStreamingIntput();
}
