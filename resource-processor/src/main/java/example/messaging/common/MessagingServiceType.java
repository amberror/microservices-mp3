package example.messaging.common;

import java.util.Arrays;


public enum MessagingServiceType {
	KAFKA("kafka"), AWS("aws");

	private final String label;

	MessagingServiceType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public static MessagingServiceType getByLabel(String searchLabel) {
		return Arrays.stream(MessagingServiceType.values())
				.filter(value -> value.getLabel().equals(searchLabel))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("MessagingServiceType with label [" + searchLabel + "] does not exists"));
	}
}
