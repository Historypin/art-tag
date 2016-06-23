package sk.eea.arttag.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import sk.eea.arttag.service.StoreService;
import sk.eea.arttag.service.StoreServiceImpl;

/**
 * Spring configuration of REST services
 * @author Maros Strmensky
 *
 */
@Configuration
public class RestConfig {

	@Bean
	public ObjectMapper objectMapper(){
		return new ObjectMapper();
	}
	
	@Bean
	public StoreService storeService(){
		return new StoreServiceImpl();
	}
}
