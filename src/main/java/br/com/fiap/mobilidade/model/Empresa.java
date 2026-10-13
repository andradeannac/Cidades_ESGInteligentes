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
@Table(name = "EMPRESAS")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empresa_seq")
    @SequenceGenerator(name = "empresa_seq", sequenceName = "SEQ_EMPRESAS", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Column(name = "NOME", nullable = false, length = 150)
    private String nome;

    @NotBlank
    @Column(name = "CNPJ", nullable = false, unique = true, length = 20)
    private String cnpj;

    @Column(name = "SETOR", length = 100)
    private String setor;

    @NotNull
    @Column(name = "PONTOS_SUSTENTAVEIS", nullable = false)
    private Integer pontosSustentaveis = 0;

    public Empresa() {
    }

    public Empresa(String nome, String cnpj, String setor, Integer pontosSustentaveis) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.setor = setor;
        this.pontosSustentaveis = pontosSustentaveis;
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public Integer getPontosSustentaveis() {
        return pontosSustentaveis;
    }

    public void setPontosSustentaveis(Integer pontosSustentaveis) {
        this.pontosSustentaveis = pontosSustentaveis;
    }
}
