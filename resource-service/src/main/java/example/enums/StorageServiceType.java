package example.enums;

import java.util.Arrays;


public enum StorageServiceType {
	LOCALSTACK("localstack"), AWS("aws");

	private final String label;

	StorageServiceType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public static StorageServiceType getByLabel(String searchLabel) {
		return Arrays.stream(StorageServiceType.values())
				.filter(value -> value.getLabel().equals(searchLabel))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("StorageServiceType with label [" + searchLabel + "] does not exist"));
	}
}
