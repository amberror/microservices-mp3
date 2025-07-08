package example.messaging.common.producers;

public interface ResourceResultProducer {
	void sendMessage(Long resourceId);
}
