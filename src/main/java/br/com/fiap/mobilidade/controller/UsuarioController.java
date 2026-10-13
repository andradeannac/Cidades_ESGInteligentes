package br.com.fiap.mobilidade.controller;

import br.com.fiap.mobilidade.model.Usuario;
import br.com.fiap.mobilidade.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
        Usuario salvo = repository.save(usuario);
        return ResponseEntity.created(URI.create("/api/usuarios/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuario.setId(id);
        return ResponseEntity.ok(repository.save(usuario));
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
