package br.com.fiap.mobilidade.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "DESLOCAMENTOS")
public class Deslocamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deslocamento_seq")
    @SequenceGenerator(name = "deslocamento_seq", sequenceName = "SEQ_DESLOCAMENTOS", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "TIPO_TRANSPORTE_ID", nullable = false)
    private TipoTransporte tipoTransporte;

    @NotNull
    @Column(name = "DATA_DESLOCAMENTO", nullable = false)
    private LocalDate dataDeslocamento;

    @Positive
    @NotNull
    @Column(name = "DISTANCIA_KM", nullable = false, precision = 10, scale = 2)
    private BigDecimal distanciaKm;

    @Column(name = "CO2_EVITADO_KG", precision = 10, scale = 2)
    private BigDecimal co2EvitadoKg;

    @Column(name = "PONTOS_GERADOS")
    private Integer pontosGerados;

    public Deslocamento() {
    }

    public Deslocamento(Usuario usuario, TipoTransporte tipoTransporte, LocalDate dataDeslocamento,
                        BigDecimal distanciaKm, BigDecimal co2EvitadoKg, Integer pontosGerados) {
        this.usuario = usuario;
        this.tipoTransporte = tipoTransporte;
        this.dataDeslocamento = dataDeslocamento;
        this.distanciaKm = distanciaKm;
        this.co2EvitadoKg = co2EvitadoKg;
        this.pontosGerados = pontosGerados;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoTransporte getTipoTransporte() {
        return tipoTransporte;
    }

    public void setTipoTransporte(TipoTransporte tipoTransporte) {
        this.tipoTransporte = tipoTransporte;
    }

    public LocalDate getDataDeslocamento() {
        return dataDeslocamento;
    }

    public void setDataDeslocamento(LocalDate dataDeslocamento) {
        this.dataDeslocamento = dataDeslocamento;
    }

    public BigDecimal getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(BigDecimal distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public BigDecimal getCo2EvitadoKg() {
        return co2EvitadoKg;
    }

    public void setCo2EvitadoKg(BigDecimal co2EvitadoKg) {
        this.co2EvitadoKg = co2EvitadoKg;
    }

    public Integer getPontosGerados() {
        return pontosGerados;
    }

    public void setPontosGerados(Integer pontosGerados) {
        this.pontosGerados = pontosGerados;
    }
}
