package example.config;

import example.converters.EntityToDTOConverter;
import example.converters.FileMetadataToSongRequestConverter;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new EntityToDTOConverter());
		registry.addConverter(new FileMetadataToSongRequestConverter());
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	@Bean
	public AutoDetectParser autoDetectParser() {
		return new AutoDetectParser();
	}

	@Bean
	public ParseContext parseContext() {
		return new ParseContext();
	}

	@Bean
	public BodyContentHandler bodyContentHandler() {
		return new BodyContentHandler();
	}
}
