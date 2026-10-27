package com.ifrn.chamados.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SetorRequest(
        @NotBlank String nome,
        @NotBlank String sigla
) {}
