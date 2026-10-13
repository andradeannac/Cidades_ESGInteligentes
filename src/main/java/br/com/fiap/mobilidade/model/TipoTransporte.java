package br.com.fiap.mobilidade.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "TIPOS_TRANSPORTE")
public class TipoTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transporte_seq")
    @SequenceGenerator(name = "transporte_seq", sequenceName = "SEQ_TIPOS_TRANSPORTE", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @NotNull
    @Column(name = "SUSTENTAVEL", nullable = false)
    private Integer sustentavel;

    @NotNull
    @Column(name = "EMISSAO_CO2_KM", nullable = false, precision = 10, scale = 4)
    private BigDecimal emissaoCo2Km;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    public TipoTransporte() {
    }

    public TipoTransporte(String nome, Integer sustentavel, BigDecimal emissaoCo2Km, String descricao) {
        this.nome = nome;
        this.sustentavel = sustentavel;
        this.emissaoCo2Km = emissaoCo2Km;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getSustentavel() {
        return sustentavel;
    }

    public void setSustentavel(Integer sustentavel) {
        this.sustentavel = sustentavel;
    }

    public BigDecimal getEmissaoCo2Km() {
        return emissaoCo2Km;
    }

    public void setEmissaoCo2Km(BigDecimal emissaoCo2Km) {
        this.emissaoCo2Km = emissaoCo2Km;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
