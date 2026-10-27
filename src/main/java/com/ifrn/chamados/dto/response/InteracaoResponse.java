package com.ifrn.chamados.dto.response;

import com.ifrn.chamados.model.Interacao;

import java.time.LocalDateTime;

public record InteracaoResponse(
        Long id,
        String texto,
        LocalDateTime dataHora,
        Long autorId,
        String autorNome
) {
    public static InteracaoResponse fromEntity(Interacao interacao) {
        return new InteracaoResponse(
                interacao.getId(),
                interacao.getTexto(),
                interacao.getDataHora(),
                interacao.getAutor().getId(),
                interacao.getAutor().getNome()
        );
    }
}
