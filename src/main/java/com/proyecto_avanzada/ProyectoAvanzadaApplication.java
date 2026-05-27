package com.proyecto_avanzada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProyectoAvanzadaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoAvanzadaApplication.class, args);
	}

	@Configuration
	public static class environmentConfig{
		
		@Bean
		public WebConfigurer corsConfigurer(){
			return new WebConfigurer(){
			
				@Override
				public void addCorsMappings(CorsRegistry registry) {
					registry.addMapping("/**")
						.allowedMethods("GET", "POST", "PUT", "DELETE");					
				}
			};			
		}
	}
		
}


