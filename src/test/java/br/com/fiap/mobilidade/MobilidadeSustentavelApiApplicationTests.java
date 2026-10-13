package br.com.fiap.mobilidade;

import br.com.fiap.mobilidade.repository.DeslocamentoRepository;
import br.com.fiap.mobilidade.repository.EmpresaRepository;
import br.com.fiap.mobilidade.repository.TipoTransporteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MobilidadeSustentavelApiApplicationTests {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private TipoTransporteRepository tipoTransporteRepository;

    @Autowired
    private DeslocamentoRepository deslocamentoRepository;

    @Test
    void contextoSobeECargaInicialEhInserida() {
        assertThat(empresaRepository.count()).isGreaterThanOrEqualTo(2);
        assertThat(tipoTransporteRepository.count()).isEqualTo(4);
        assertThat(deslocamentoRepository.count()).isGreaterThanOrEqualTo(3);
    }
}
