package br.com.Peixaria.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import br.com.Peixaria.model.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, String>{

	Usuario findById(Long id);
	Usuario findByEmail(String email);
	Usuario findByResetToken(String resetToken);
	
	@Query(value = "SELECT * FROM usuario	"+
					"	WHERE  email = :email		"+
					"		AND senha= :senha		",	nativeQuery= true)
	public Usuario login(String email, String senha);
}
