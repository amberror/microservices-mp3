package example.services;

import jakarta.validation.constraints.NotNull;


public interface ConstraintsService {
	void checkIdConstraints(@NotNull Long id);
	void checkInlineStringIdsConstraints(@NotNull String ids);
}
