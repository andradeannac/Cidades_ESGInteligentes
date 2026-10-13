package br.com.fiap.mobilidade.controller;

import br.com.fiap.mobilidade.model.Deslocamento;
import br.com.fiap.mobilidade.repository.DeslocamentoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/deslocamentos")
public class DeslocamentoController {

    private final DeslocamentoRepository repository;

    public DeslocamentoController(DeslocamentoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Deslocamento> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deslocamento> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Deslocamento> cadastrar(@Valid @RequestBody Deslocamento deslocamento) {
        Deslocamento salvo = repository.save(deslocamento);
        return ResponseEntity.created(URI.create("/api/deslocamentos/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deslocamento> atualizar(@PathVariable Long id, @Valid @RequestBody Deslocamento deslocamento) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        deslocamento.setId(id);
        return ResponseEntity.ok(repository.save(deslocamento));
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
