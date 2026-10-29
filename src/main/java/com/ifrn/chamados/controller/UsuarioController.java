package com.ifrn.chamados.controller;

import com.ifrn.chamados.dto.request.UsuarioRequest;
import com.ifrn.chamados.dto.response.ChamadoResponse;
import com.ifrn.chamados.dto.response.UsuarioResponse;
import com.ifrn.chamados.model.StatusChamado;
import com.ifrn.chamados.model.TipoUsuario;
import com.ifrn.chamados.service.ChamadoService;
import com.ifrn.chamados.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ChamadoService chamadoService;

    public UsuarioController(UsuarioService usuarioService, ChamadoService chamadoService) {
        this.usuarioService = usuarioService;
        this.chamadoService = chamadoService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(request));
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoUsuario tipo,
            @RequestParam(required = false) Long setorId,
            @PageableDefault(size = 10) Pageable pageable) {
        pageable = limitarTamanho(pageable);
        return ResponseEntity.ok(usuarioService.listar(nome, tipo, setorId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.atualizar(id, request));
    }

    @GetMapping("/{id}/chamados")
    public ResponseEntity<Page<ChamadoResponse>> listarChamados(
            @PathVariable Long id,
            @RequestParam(required = false) StatusChamado status,
            @PageableDefault(size = 10) Pageable pageable) {
        pageable = limitarTamanho(pageable);
        return ResponseEntity.ok(chamadoService.listarPorUsuario(id, status, pageable));
    }

    private Pageable limitarTamanho(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), 50);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }
}
