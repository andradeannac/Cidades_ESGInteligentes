package br.com.fiap.mobilidade.repository;

import br.com.fiap.mobilidade.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
