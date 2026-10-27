package com.ifrn.chamados.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResolverChamadoRequest(
        @NotNull Long tecnicoId,
        @NotBlank String solucao
) {}
