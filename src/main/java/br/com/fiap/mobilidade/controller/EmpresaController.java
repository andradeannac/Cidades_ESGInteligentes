package br.com.fiap.mobilidade.controller;

import br.com.fiap.mobilidade.model.Empresa;
import br.com.fiap.mobilidade.repository.EmpresaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaRepository repository;

    public EmpresaController(EmpresaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Empresa> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Empresa> cadastrar(@Valid @RequestBody Empresa empresa) {
        Empresa salvo = repository.save(empresa);
        return ResponseEntity.created(URI.create("/api/empresas/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> atualizar(@PathVariable Long id, @Valid @RequestBody Empresa empresa) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        empresa.setId(id);
        return ResponseEntity.ok(repository.save(empresa));
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
