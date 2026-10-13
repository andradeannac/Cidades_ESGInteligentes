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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESGATES_RECOMPENSA")
public class ResgateRecompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resgate_seq")
    @SequenceGenerator(name = "resgate_seq", sequenceName = "SEQ_RESGATES_RECOMPENSA", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "RECOMPENSA_ID", nullable = false)
    private Recompensa recompensa;

    @NotNull
    @Column(name = "DATA_RESGATE", nullable = false)
    private LocalDateTime dataResgate;

    @NotBlank
    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    public ResgateRecompensa() {
    }

    public ResgateRecompensa(Usuario usuario, Recompensa recompensa, LocalDateTime dataResgate, String status) {
        this.usuario = usuario;
        this.recompensa = recompensa;
        this.dataResgate = dataResgate;
        this.status = status;
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

    public Recompensa getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(Recompensa recompensa) {
        this.recompensa = recompensa;
    }

    public LocalDateTime getDataResgate() {
        return dataResgate;
    }

    public void setDataResgate(LocalDateTime dataResgate) {
        this.dataResgate = dataResgate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
