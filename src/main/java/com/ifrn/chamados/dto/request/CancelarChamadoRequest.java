package com.ifrn.chamados.dto.request;

import jakarta.validation.constraints.NotNull;

public record CancelarChamadoRequest(
        @NotNull Long solicitanteId
) {}
