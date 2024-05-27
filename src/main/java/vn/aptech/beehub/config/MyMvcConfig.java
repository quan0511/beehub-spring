package vn.aptech.beehub.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class MyMvcConfig {
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper model = new ModelMapper();
		model.getConfiguration().setFieldMatchingEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(false);
		return model;
	}
	
}
