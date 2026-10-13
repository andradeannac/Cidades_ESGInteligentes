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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "USUARIOS")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "SEQ_USUARIOS", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Column(name = "NOME", nullable = false, length = 150)
    private String nome;

    @Email
    @NotBlank
    @Column(name = "EMAIL", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @Column(name = "CPF", nullable = false, unique = true, length = 14)
    private String cpf;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "EMPRESA_ID", nullable = false)
    private Empresa empresa;

    @NotNull
    @Column(name = "PONTOS_ACUMULADOS", nullable = false)
    private Integer pontosAcumulados = 0;

    public Usuario() {
    }

    public Usuario(String nome, String email, String cpf, Empresa empresa, Integer pontosAcumulados) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.empresa = empresa;
        this.pontosAcumulados = pontosAcumulados;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Integer getPontosAcumulados() {
        return pontosAcumulados;
    }

    public void setPontosAcumulados(Integer pontosAcumulados) {
        this.pontosAcumulados = pontosAcumulados;
    }
}
