package com.ifrn.chamados.controller;

import com.ifrn.chamados.dto.request.*;
import com.ifrn.chamados.dto.response.ChamadoDetalhadoResponse;
import com.ifrn.chamados.dto.response.ChamadoResponse;
import com.ifrn.chamados.dto.response.InteracaoResponse;
import com.ifrn.chamados.model.Prioridade;
import com.ifrn.chamados.model.StatusChamado;
import com.ifrn.chamados.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponse> criar(@Valid @RequestBody ChamadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chamadoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<Page<ChamadoResponse>> listar(
            @RequestParam(required = false) StatusChamado status,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) Long setorId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long solicitanteId,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @PageableDefault(size = 10, sort = "dataAbertura") Pageable pageable) {
        pageable = limitarTamanho(pageable);
        return ResponseEntity.ok(chamadoService.listar(status, prioridade, setorId, categoriaId,
                solicitanteId, tecnicoId, dataInicio, dataFim, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoDetalhadoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(chamadoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/assumir")
    public ResponseEntity<ChamadoResponse> assumir(@PathVariable Long id,
                                                    @Valid @RequestBody AssumirChamadoRequest request) {
        return ResponseEntity.ok(chamadoService.assumir(id, request));
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<ChamadoResponse> resolver(@PathVariable Long id,
                                                     @Valid @RequestBody ResolverChamadoRequest request) {
        return ResponseEntity.ok(chamadoService.resolver(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ChamadoResponse> cancelar(@PathVariable Long id,
                                                     @Valid @RequestBody CancelarChamadoRequest request) {
        return ResponseEntity.ok(chamadoService.cancelar(id, request));
    }

    @PostMapping("/{id}/interacoes")
    public ResponseEntity<InteracaoResponse> adicionarInteracao(
            @PathVariable Long id,
            @Valid @RequestBody InteracaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chamadoService.adicionarInteracao(id, request));
    }

    private Pageable limitarTamanho(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), 50);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }
}
