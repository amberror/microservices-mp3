package example.converters;

import example.dto.ResourceDTO;
import example.entities.ResourceEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class EntityToDTOConverter implements Converter<ResourceEntity, ResourceDTO> {

	@Override
	public ResourceDTO convert(ResourceEntity source) {
		return ResourceDTO.builder()
				.id(source.getId())
				.data(Optional.ofNullable(source.getData()).orElse(new byte[0]))
				.build();
	}

}
