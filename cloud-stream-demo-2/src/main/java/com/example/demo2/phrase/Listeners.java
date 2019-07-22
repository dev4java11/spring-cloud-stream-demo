package com.example.demo2.phrase;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Listeners {

	@StreamListener(Channels.PHRASE_STREAMING_INPUT)
	@SendTo(Channels.TOTAL_PHRASE_STREAMING_OUTPUT)
	public KStream<?, PhraseCount> countPhrasesStreaming(KStream<?, Phrase> stream) {
		log.debug("INVOKE STREAMING COUNT PHRASES");
		KStream<String, PhraseCount> stream_table = stream
				.map((k, v) -> new KeyValue<>(v, v))
				.groupByKey()
				.windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(5)))
				.count(Materialized.as(Channels.TOTAL_PHRASE_STREAMING_OUTPUT))
				.toStream()
				.map((k,v) -> new KeyValue<>(k.key().getId(), new PhraseCount(k.key().getId(), k.key().getPhrase(), v, new Date(k.window().start()), new Date(k.window().end()))));
		return stream_table;
	}
	
	@StreamListener(Channels.TOTAL_PHRASE_STREAMING_INPUT)
	public void countPhrasesTable(KTable<?, PhraseCount> table) {
		log.info("READ TABLE COUNT PHRASES ######################################################################");
		table
			.toStream()
			.foreach((k, v) -> log.info("This phrase: " + v.getPhrase() + " has total: " + v.getCount()));
		log.info("READ TABLE COUNT PHRASES ######################################################################");
	}

	@StreamListener(target = Channels.PHRASE_INPUT)
	public void on(Phrase evt) {
		log.info("CONSUMED EVENT: " + evt.toString());
	}
}
