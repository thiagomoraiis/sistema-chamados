package com.ifrn.chamados.controller;

import com.ifrn.chamados.dto.request.SetorRequest;
import com.ifrn.chamados.dto.response.CategoriaResponse;
import com.ifrn.chamados.dto.response.SetorResponse;
import com.ifrn.chamados.service.CategoriaService;
import com.ifrn.chamados.service.SetorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setores")
public class SetorController {

    private final SetorService setorService;
    private final CategoriaService categoriaService;

    public SetorController(SetorService setorService, CategoriaService categoriaService) {
        this.setorService = setorService;
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<SetorResponse> criar(@Valid @RequestBody SetorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(setorService.criar(request));
    }

    @GetMapping
    public ResponseEntity<Page<SetorResponse>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10) Pageable pageable) {
        pageable = limitarTamanho(pageable);
        return ResponseEntity.ok(setorService.listar(nome, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(setorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetorResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody SetorRequest request) {
        return ResponseEntity.ok(setorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        setorService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/categorias")
    public ResponseEntity<Page<CategoriaResponse>> listarCategorias(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        pageable = limitarTamanho(pageable);
        return ResponseEntity.ok(categoriaService.listarPorSetor(id, pageable));
    }

    private Pageable limitarTamanho(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), 50);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }
}
