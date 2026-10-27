package com.ifrn.chamados.dto.response;

import java.time.LocalDateTime;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String mensagem,
        String caminho
) {}
