package pe.gob.reniec.sgrd.ksdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pe.gob.reniec.sgrd.ksdemo.KafkaStreamsDemoApplication.VoteBinding;

@SpringBootApplication
@EnableBinding(VoteBinding.class)
@Slf4j
public class KafkaStreamsDemoApplication {
	
	@Autowired
	private VoteBinding binging;

	public static void main(String[] args) {
		SpringApplication.run(KafkaStreamsDemoApplication.class, args);
	}
	
	public class VoteCountEvtSerde extends JsonSerde<VoteCountEvt> {
		
	}

	@Bean
	public ApplicationRunner publish() {
		return args -> {
			List<VoteEvt> votes = Arrays.asList(
					new VoteEvt("1111", "George Washington"), 
					new VoteEvt("2222", "Abraham Lincoln"), 
					new VoteEvt("3333", "Jimmy Carter"), 
					new VoteEvt("4444", "Gerald Ford"), 
					new VoteEvt("5555", "Richard Nixon"));
			Random rnd = new Random();
			Runnable runnable = () -> {
				try {
					VoteEvt evt = votes.get(rnd.nextInt(votes.size()));
					Message<VoteEvt> message = MessageBuilder
							.withPayload(evt)
							.setHeader(KafkaHeaders.MESSAGE_KEY, evt.getId())
							.build();
					binging.voteOut().send(message);
				}catch(Exception ex) {
					log.error("Error for send message to voteOut ", ex);
				}
			};
			Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(runnable, 3, 3, TimeUnit.SECONDS);
		};
	}
	
	@Bean
	public Consumer<KStream<String, VoteEvt>> log() {
		return streamIn -> streamIn
				.foreach((k, v) -> log.debug("Consume from stream Key: " + k + " - Value: " + v.toString()));
	}
	
	@Bean
	public Function<KStream<String, VoteEvt>, KStream<String, VoteCountEvt>> count() {
		return streamIn -> streamIn
				.map((k, v) -> new KeyValue<>(v.getId() + "_" + v.getName(), new Long(0)))
				.groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
//				.groupByKey() <- ERROR SERIALIZATION FOR MATERIALIZED VIEW MV_VOTE_COUNT
				.count(Materialized.as(VoteBinding.MV_VOTE_COUNT))
				.mapValues((k, v) -> new VoteCountEvt(k.substring(0, k.indexOf("_")), k.substring(k.indexOf("_") + 1), v)
						,Materialized.<String, VoteCountEvt, KeyValueStore<Bytes, byte[]>>as(VoteBinding.MV_VOTE_COUNT_JSON)
						.withKeySerde(Serdes.String())
						.withValueSerde(new VoteCountEvtSerde()))
				.toStream();
	}
	
	@Bean
	public Consumer<KTable<String, VoteCountEvt>> logcount(){
		return tableIn -> tableIn
				.toStream()
				.foreach((k, v) -> log.info("total: " + v.toString()));
	}
	
	@RestController
	@RequestMapping("/api")
	public class Api {
		
		@Autowired
		private InteractiveQueryService interactiveQueryService;
		
		@GetMapping("/count")
		public List<VoteCountEvt> count() {
			List<VoteCountEvt> list = new ArrayList<VoteCountEvt>();
			ReadOnlyKeyValueStore<String, VoteCountEvt> store =	interactiveQueryService.getQueryableStore(VoteBinding.MV_VOTE_COUNT_JSON, QueryableStoreTypes.keyValueStore());
			KeyValueIterator<String, VoteCountEvt> iterator = store.all();
			while(iterator.hasNext()) {
				KeyValue<String, VoteCountEvt> kv = iterator.next();
				list.add(kv.value);
			}
			return list;
		}
	}
	
	public interface VoteBinding {
		
		public static final String MV_VOTE_COUNT = "mv-vote-count";
		
		public static final String MV_VOTE_COUNT_JSON = "mv-vote-count-json";
		
		public static final String VOTE_OUT = "voteOut";
		
		@Output(VOTE_OUT)
		MessageChannel voteOut();
	}
}
