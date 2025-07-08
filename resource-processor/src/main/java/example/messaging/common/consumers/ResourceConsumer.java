package example.messaging.common.consumers;

public interface ResourceConsumer {
	void consumeMessage(Long key, Long message, String topic);
	void consumeMessageDlt(Long key, Long message, String topic);
}
