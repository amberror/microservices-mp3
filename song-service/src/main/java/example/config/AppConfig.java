package example.config;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;


@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

	@Autowired
	private Set<Converter> customConverters;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		customConverters.forEach(registry::addConverter);
		/*
		registry.addConverter(new EntityToResponseDTOConverter());
		registry.addConverter(new RequestDTOToEntityConverter());
		*/
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	@Bean
	public RestTemplate resourceRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public Tika tika() {
		return new Tika();
	}
}
