package com.ifrn.chamados.dto.request;

import jakarta.validation.constraints.NotNull;

public record AssumirChamadoRequest(
        @NotNull Long tecnicoId
) {}
