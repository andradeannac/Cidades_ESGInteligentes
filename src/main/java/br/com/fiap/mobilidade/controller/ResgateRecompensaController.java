package br.com.fiap.mobilidade.controller;

import br.com.fiap.mobilidade.model.ResgateRecompensa;
import br.com.fiap.mobilidade.repository.ResgateRecompensaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/resgates-recompensa")
public class ResgateRecompensaController {

    private final ResgateRecompensaRepository repository;

    public ResgateRecompensaController(ResgateRecompensaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ResgateRecompensa> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResgateRecompensa> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ResgateRecompensa> cadastrar(@Valid @RequestBody ResgateRecompensa resgate) {
        ResgateRecompensa salvo = repository.save(resgate);
        return ResponseEntity.created(URI.create("/api/resgates-recompensa/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResgateRecompensa> atualizar(@PathVariable Long id, @Valid @RequestBody ResgateRecompensa resgate) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        resgate.setId(id);
        return ResponseEntity.ok(repository.save(resgate));
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
