package br.com.Peixaria.service.autenticator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginInterceptorAppConfig  implements WebMvcConfigurer{

	@Autowired
	private LoginInterceptor loginInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(loginInterceptor)
	        .excludePathPatterns(
	            "/login",
	            "/logar",
	            "/erro",
	            "/registro",
	            "/usuarioFront/**", // libera tudo da pasta usuarioFront
	            "/static/**",        // libera recursos estáticos também (por garantia)
	            "/css/**",           // se tiver css em outra pasta
	            "/js/**",            // se tiver js
	            "/images/**"         // se tiver imagens
	        );
	}

}
