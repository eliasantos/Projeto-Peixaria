package br.com.Peixaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.Peixaria.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	Usuario findByEmail(String email);
	Usuario findByResetToken(String resetToken);
	
	@Query(value = "SELECT * FROM usuario	"+
					"	WHERE  email = :email		"+
					"		AND senha= :senha		",	nativeQuery= true)
	public Usuario login(String email, String senha);
}
