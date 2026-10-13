package br.com.fiap.mobilidade.config;

import br.com.fiap.mobilidade.model.Deslocamento;
import br.com.fiap.mobilidade.model.Empresa;
import br.com.fiap.mobilidade.model.Recompensa;
import br.com.fiap.mobilidade.model.ResgateRecompensa;
import br.com.fiap.mobilidade.model.TipoTransporte;
import br.com.fiap.mobilidade.model.Usuario;
import br.com.fiap.mobilidade.repository.DeslocamentoRepository;
import br.com.fiap.mobilidade.repository.EmpresaRepository;
import br.com.fiap.mobilidade.repository.RecompensaRepository;
import br.com.fiap.mobilidade.repository.ResgateRecompensaRepository;
import br.com.fiap.mobilidade.repository.TipoTransporteRepository;
import br.com.fiap.mobilidade.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class CargaInicialBanco {

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner inserirDadosIniciais(
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            TipoTransporteRepository tipoTransporteRepository,
            DeslocamentoRepository deslocamentoRepository,
            RecompensaRepository recompensaRepository,
            ResgateRecompensaRepository resgateRecompensaRepository
    ) {
        return args -> {
            if (empresaRepository.count() > 0) {
                System.out.println("Carga inicial ignorada: banco ja possui dados.");
                return;
            }

            Empresa empresa1 = empresaRepository.save(new Empresa(
                    "EcoMove Tecnologia",
                    "12.345.678/0001-90",
                    "Tecnologia",
                    850
            ));

            Empresa empresa2 = empresaRepository.save(new Empresa(
                    "Construtora Verde",
                    "98.765.432/0001-10",
                    "Construcao Civil",
                    620
            ));

            Usuario usuario1 = usuarioRepository.save(new Usuario(
                    "Ana Souza",
                    "ana.souza@email.com",
                    "123.456.789-00",
                    empresa1,
                    300
            ));

            Usuario usuario2 = usuarioRepository.save(new Usuario(
                    "Carlos Lima",
                    "carlos.lima@email.com",
                    "987.654.321-00",
                    empresa2,
                    180
            ));

            TipoTransporte bicicleta = tipoTransporteRepository.save(new TipoTransporte(
                    "Bicicleta",
                    1,
                    new BigDecimal("0.0000"),
                    "Transporte sem emissao direta de CO2"
            ));

            TipoTransporte carroEletrico = tipoTransporteRepository.save(new TipoTransporte(
                    "Carro eletrico",
                    1,
                    new BigDecimal("0.0300"),
                    "Veiculo eletrico de baixa emissao"
            ));

            TipoTransporte onibus = tipoTransporteRepository.save(new TipoTransporte(
                    "Onibus coletivo",
                    1,
                    new BigDecimal("0.0800"),
                    "Transporte coletivo com menor emissao por passageiro"
            ));

            tipoTransporteRepository.save(new TipoTransporte(
                    "Carro a combustao",
                    0,
                    new BigDecimal("0.1800"),
                    "Veiculo movido a combustivel fossil"
            ));

            deslocamentoRepository.save(new Deslocamento(
                    usuario1,
                    bicicleta,
                    LocalDate.now().minusDays(2),
                    new BigDecimal("8.50"),
                    new BigDecimal("1.53"),
                    85
            ));

            deslocamentoRepository.save(new Deslocamento(
                    usuario2,
                    carroEletrico,
                    LocalDate.now().minusDays(1),
                    new BigDecimal("22.00"),
                    new BigDecimal("3.30"),
                    120
            ));

            deslocamentoRepository.save(new Deslocamento(
                    usuario1,
                    onibus,
                    LocalDate.now(),
                    new BigDecimal("15.00"),
                    new BigDecimal("1.50"),
                    60
            ));

            Recompensa recompensa1 = recompensaRepository.save(new Recompensa(
                    "Cupom de desconto",
                    "Cupom de R$ 25,00 para usuarios com mobilidade sustentavel",
                    200,
                    1
            ));

            recompensaRepository.save(new Recompensa(
                    "Dia de home office",
                    "Beneficio concedido para colaboradores com alta pontuacao sustentavel",
                    500,
                    1
            ));

            recompensaRepository.save(new Recompensa(
                    "Voucher transporte",
                    "Voucher para uso em transporte publico ou bicicleta compartilhada",
                    350,
                    1
            ));

            resgateRecompensaRepository.save(new ResgateRecompensa(
                    usuario1,
                    recompensa1,
                    LocalDateTime.now(),
                    "APROVADO"
            ));

            System.out.println("Carga inicial inserida com sucesso via JPA.");
        };
    }
}
