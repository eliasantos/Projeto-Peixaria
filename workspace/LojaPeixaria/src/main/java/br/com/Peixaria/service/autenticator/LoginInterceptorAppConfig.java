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
                "/esqueci-senha", // Exclua a página de esqueci senha
                "/usuarioFront/loginStyle.css", // Libere os estilos da página de login
                "/usuarioFront/registroStyle.css", // Libere os estilos da página de registro
                "/usuarioFront/banca.svg", // Libere as imagens da página de login
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**"
            );
    }

}