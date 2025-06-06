package example.enums;

import lombok.Getter;


@Getter
public enum PermissionType {
	USER("USER"), ADMIN("ADMIN");

	private final String label;

	PermissionType(String label) {
		this.label = label;
	}
}
