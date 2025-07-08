package example.messaging.common.consumers;


public interface ResourceResultConsumer {
	void consumeMessage(Long key, Long message, String topic);
	void consumeMessageDlt(Long key, Long message, String topic);
}
