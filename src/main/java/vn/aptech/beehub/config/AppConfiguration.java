package vn.aptech.beehub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
public class AppConfiguration {

	@Bean
		public ModelMapper modelMapper() {
			 ModelMapper mapper = new ModelMapper();
			 mapper.getConfiguration()
			 .setFieldMatchingEnabled(true)
			 .setMatchingStrategy(MatchingStrategies.STRICT);
			 return mapper;

		}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
		objectMapper.setDateFormat(df);
		return objectMapper;
	}
}
