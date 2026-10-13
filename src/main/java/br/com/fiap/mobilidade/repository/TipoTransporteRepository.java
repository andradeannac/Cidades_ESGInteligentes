package br.com.fiap.mobilidade.repository;

import br.com.fiap.mobilidade.model.TipoTransporte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoTransporteRepository extends JpaRepository<TipoTransporte, Long> {
}
