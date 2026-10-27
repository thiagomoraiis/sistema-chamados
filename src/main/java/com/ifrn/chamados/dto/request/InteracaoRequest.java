package com.ifrn.chamados.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InteracaoRequest(
        @NotBlank String texto,
        @NotNull Long autorId
) {}
