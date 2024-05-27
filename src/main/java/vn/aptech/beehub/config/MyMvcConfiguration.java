package vn.aptech.beehub.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MyMvcConfiguration implements WebMvcConfigurer {
	String UPLOAD_DIR = System.getProperty("user.dir") +  "/static/";
	@Override
	 public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/public/**").addResourceLocations("file:///" + UPLOAD_DIR);
			WebMvcConfigurer.super.addResourceHandlers(registry);
		}
//	@Bean
//		public ModelMapper modelMapper() {
//			 ModelMapper mapper = new ModelMapper();
//			 mapper.getConfiguration()
//			 .setFieldMatchingEnabled(true)
//			 .setMatchingStrategy(MatchingStrategies.STRICT);
//			 return mapper;
//			 
//		}
	@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedHeaders("*");
		}
}
