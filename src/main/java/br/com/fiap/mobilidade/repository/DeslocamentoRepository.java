package br.com.fiap.mobilidade.repository;

import br.com.fiap.mobilidade.model.Deslocamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeslocamentoRepository extends JpaRepository<Deslocamento, Long> {
}
