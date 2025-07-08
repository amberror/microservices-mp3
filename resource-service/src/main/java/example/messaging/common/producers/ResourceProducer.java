package example.messaging.common.producers;


public interface ResourceProducer {
	void sendMessage(Long resourceId);
}
