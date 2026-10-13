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

@Entity
@Table(name = "RECOMPENSAS")
public class Recompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recompensa_seq")
    @SequenceGenerator(name = "recompensa_seq", sequenceName = "SEQ_RECOMPENSAS", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Column(name = "NOME", nullable = false, length = 120)
    private String nome;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @NotNull
    @Column(name = "PONTOS_NECESSARIOS", nullable = false)
    private Integer pontosNecessarios;

    @NotNull
    @Column(name = "ATIVA", nullable = false)
    private Integer ativa;

    public Recompensa() {
    }

    public Recompensa(String nome, String descricao, Integer pontosNecessarios, Integer ativa) {
        this.nome = nome;
        this.descricao = descricao;
        this.pontosNecessarios = pontosNecessarios;
        this.ativa = ativa;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getPontosNecessarios() {
        return pontosNecessarios;
    }

    public void setPontosNecessarios(Integer pontosNecessarios) {
        this.pontosNecessarios = pontosNecessarios;
    }

    public Integer getAtiva() {
        return ativa;
    }

    public void setAtiva(Integer ativa) {
        this.ativa = ativa;
    }
}
