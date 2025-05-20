package contracts.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class KafkaMessageVerifier implements MessageVerifierReceiver<Message<?>> {

	private static final Log LOG = LogFactory.getLog(KafkaMessageVerifier.class);

	Map<String, BlockingQueue<Message<Long>>> broker = new ConcurrentHashMap<>();


	@Override
	public Message receive(String destination, long timeout, TimeUnit timeUnit, @Nullable YamlContract contract) {
		broker.putIfAbsent(destination, new ArrayBlockingQueue<>(1));
		BlockingQueue<Message<Long>> messageQueue = broker.get(destination);
		Message<?> message;
		try {
			message = messageQueue.poll(timeout, timeUnit);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		if (message != null) {
			LOG.info("Removed a message from a topic [" + destination + "]");
		}
		return message;
	}


	@KafkaListener(id = "resourceTopicListener", topicPattern = ".*", containerFactory = "testResourceKafkaListenerContainerFactory")
	public void listen(ConsumerRecord payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		LOG.info("Got a message from a topic [" + topic + "]");
		Map<String, Object> headers = new HashMap<>() {{
			put(KafkaHeaders.KEY, payload.key());
		}};
		new DefaultKafkaHeaderMapper().toHeaders(payload.headers(), headers);
		broker.putIfAbsent(topic, new ArrayBlockingQueue<>(1));
		BlockingQueue<Message<Long>> messageQueue = broker.get(topic);
		LOG.info("Got a message [" + payload + "]");
		messageQueue.add(MessageBuilder.createMessage((Long) payload.value(), new MessageHeaders(headers)));
	}

	@Override
	public Message receive(String destination, YamlContract contract) {
		return receive(destination, 15, TimeUnit.SECONDS, contract);
	}
}
