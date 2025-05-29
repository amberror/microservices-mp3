package example.services;

import org.springframework.core.convert.ConversionService;

import java.util.List;


public interface StorageConversionService extends ConversionService {
	<T, S> List<T> convertAll(List<S> source, Class<T> targetType);
}
