package br.com.fiap.mobilidade.controller;

import br.com.fiap.mobilidade.model.TipoTransporte;
import br.com.fiap.mobilidade.repository.TipoTransporteRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-transporte")
public class TipoTransporteController {

    private final TipoTransporteRepository repository;

    public TipoTransporteController(TipoTransporteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TipoTransporte> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoTransporte> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoTransporte> cadastrar(@Valid @RequestBody TipoTransporte tipoTransporte) {
        TipoTransporte salvo = repository.save(tipoTransporte);
        return ResponseEntity.created(URI.create("/api/tipos-transporte/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoTransporte> atualizar(@PathVariable Long id, @Valid @RequestBody TipoTransporte tipoTransporte) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tipoTransporte.setId(id);
        return ResponseEntity.ok(repository.save(tipoTransporte));
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
