package br.com.fiap.mobilidade.controller;

import br.com.fiap.mobilidade.model.Recompensa;
import br.com.fiap.mobilidade.repository.RecompensaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recompensas")
public class RecompensaController {

    private final RecompensaRepository repository;

    public RecompensaController(RecompensaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Recompensa> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recompensa> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Recompensa> cadastrar(@Valid @RequestBody Recompensa recompensa) {
        Recompensa salvo = repository.save(recompensa);
        return ResponseEntity.created(URI.create("/api/recompensas/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recompensa> atualizar(@PathVariable Long id, @Valid @RequestBody Recompensa recompensa) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        recompensa.setId(id);
        return ResponseEntity.ok(repository.save(recompensa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
