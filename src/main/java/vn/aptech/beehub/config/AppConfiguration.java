package vn.aptech.beehub.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfiguration implements WebMvcConfigurer {

	@Bean
		public ModelMapper modelMapper() {
			 ModelMapper mapper = new ModelMapper();
			 mapper.getConfiguration()
			 .setFieldMatchingEnabled(true)
			 .setMatchingStrategy(MatchingStrategies.STRICT);
			 return mapper;

		}
}
