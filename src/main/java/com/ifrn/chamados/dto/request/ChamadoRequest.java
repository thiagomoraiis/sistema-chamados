package com.ifrn.chamados.dto.request;

import com.ifrn.chamados.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChamadoRequest(
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotBlank String local,
        Prioridade prioridade,
        @NotNull Long categoriaId,
        @NotNull Long solicitanteId
) {}
