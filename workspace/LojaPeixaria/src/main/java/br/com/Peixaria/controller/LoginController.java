package br.com.Peixaria.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.Peixaria.model.Usuario;
import br.com.Peixaria.repository.UsuarioRepository;
import br.com.Peixaria.service.CookieService;
import br.com.Peixaria.service.ResetSenhaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class LoginController {

	@Autowired
	private UsuarioRepository ur;
	
	@Autowired
	private ResetSenhaService resetSenhaService;

	
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@GetMapping("/login")
	public String login() {
		return "usuarioFront/login";
	}
	
	@GetMapping("/")
	public String dashboard(Model model, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
	    String usuarioId = CookieService.getCookie(request, "usuarioId");

	    if (usuarioId == null) {
	        return "redirect:/login";
	    }

	    try {
	        Long id = Long.parseLong(usuarioId);
	        Usuario usuario = ur.findById(id).orElse(null);

	        if (usuario == null) {
	            CookieService.deleteCookie(response, "usuarioId");
	            return "redirect:/login";
	        }

	        model.addAttribute("nome", usuario.getNome());
	        model.addAttribute("email", usuario.getEmail());
	        return "usuarioFront/index";

	    } catch (NumberFormatException e) {
	        // Se o ID do cookie for inválido
	        CookieService.deleteCookie(response, "usuarioId");
	        return "redirect:/login";
	    }
	}
	
	@PostMapping("/logar")
	public String loginUsuario(Usuario usuario, Model model, HttpServletResponse response) throws UnsupportedEncodingException {
		
		Usuario usuarioLogado = this.ur.findByEmail(usuario.getEmail());
		
		if(usuarioLogado != null && passwordEncoder.matches(usuario.getSenha(), usuarioLogado.getSenha())) {
			CookieService.setCookie(response, "usuarioId", String.valueOf(usuarioLogado.getId()), 10000);
			CookieService.setCookie(response, "nomeUsuario", String.valueOf(usuarioLogado.getNome()), 10000);
			return "redirect:/";
		}
		
		model.addAttribute("erro", "Usuário inválido!");
		return "usuarioFront/login";
	}
	
	@GetMapping("/registro")
    public String registro() {
        return "usuarioFront/registro"; 
    }

	@RequestMapping(value = "/registro", method = RequestMethod.POST)
	public String registroUsuario(@Valid Usuario usuario, BindingResult result) {
		
		if(result.hasErrors()){
			return "redirect:/registro";
		}	
		
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		
		ur.save(usuario);
		return "redirect:/login";
	}
	
	@GetMapping("/esqueci-senha")
	public String formEsqueciSenha() {
	    return "usuarioFront/solicitarResetSenha";
	}

	@PostMapping("/esqueci-senha")
	public String enviarTokenEmail(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
	    Usuario usuario = ur.findByEmail(email);
	    
	    if (usuario == null) {
	        redirectAttributes.addFlashAttribute("mensagem", "E-mail não encontrado!");
	        return "redirect:/solicitarResetSenha";
	    }
	    
	    String token = UUID.randomUUID().toString();
	    usuario.setResetToken(token);
	    usuario.setTokenExpiration(LocalDateTime.now().plusHours(1)); // Token válido por 1 hora
	    ur.save(usuario);

	    resetSenhaService.enviarEmailComToken(usuario.getEmail(), token);

	    return "usuarioFront/esqueciSenha";
	}

	@GetMapping("/resetar-senha")
	public String formResetarSenha() {
	    return "usuarioFront/esqueciSenha"; // agora sim vai para página de redefinir senha
	}

	@PostMapping("/resetar-senha")
	public String processaResetarSenha(HttpServletRequest request, Model model) {
	    String token = request.getParameter("token");
	    String novaSenha = request.getParameter("senha");

	    Usuario usuario = ur.findByResetToken(token);

	    if (usuario == null || usuario.getTokenExpiration().isBefore(LocalDateTime.now())) {
	        model.addAttribute("mensagem", "Token inválido ou expirado.");
	        return "usuarioFront/esqueciSenha";
	    }

	    usuario.setSenha(passwordEncoder.encode(novaSenha));
	    usuario.setResetToken(null);
	    usuario.setTokenExpiration(null);
	    ur.save(usuario);

	    model.addAttribute("mensagem", "Senha alterada com sucesso. Faça login.");
	    return "usuarioFront/login";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
	    CookieService.deleteCookie(response, "usuarioId"); // Apaga o cookie
	    return "redirect:/login"; // Volta pra tela de login
	}


}
