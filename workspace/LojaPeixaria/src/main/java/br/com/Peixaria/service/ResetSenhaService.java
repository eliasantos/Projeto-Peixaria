package br.com.Peixaria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ResetSenhaService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailComToken(String destinatario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("elias.santos.tijobs@gmail.com"); 
        message.setTo(destinatario);
        message.setSubject("Token de Redefinição de Senha");
        message.setText("Aqui está o seu token para redefinir sua senha: " + token);

        mailSender.send(message);
    }
}
