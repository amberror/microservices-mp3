package example.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum PermissionType {
	ADMIN("ADMIN", List.of()), USER("USER", List.of(ADMIN));

	private final String label;

	private final List<PermissionType> higherRoles;

	PermissionType(String label, List<PermissionType> higherRoles) {
		this.label = label;
		this.higherRoles = higherRoles;
	}

	public boolean isHigherRole(PermissionType permissionType) {
		return this.higherRoles.stream().anyMatch(role -> role.equals(permissionType));
	}
}
