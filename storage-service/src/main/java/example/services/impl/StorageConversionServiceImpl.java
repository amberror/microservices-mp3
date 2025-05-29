package example.services.impl;

import example.services.StorageConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;


@Service
public class StorageConversionServiceImpl extends ApplicationConversionService implements StorageConversionService {

	private final Set<Converter> customConverters;

	@Autowired
	public StorageConversionServiceImpl(Set<Converter> customConverters) {
		super();
		this.customConverters = customConverters;
		this.customConverters.forEach(super::addConverter);
	}

	public <T, S> List<T> convertAll(List<S> source, Class<T> targetType) {
		Assert.notNull(targetType, "Target type to convert to cannot be null");
		return source.stream().map(item -> super.convert(item, targetType)).toList();
	}

}
