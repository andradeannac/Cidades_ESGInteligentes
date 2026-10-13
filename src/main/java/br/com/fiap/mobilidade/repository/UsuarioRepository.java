package br.com.fiap.mobilidade.repository;

import br.com.fiap.mobilidade.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
