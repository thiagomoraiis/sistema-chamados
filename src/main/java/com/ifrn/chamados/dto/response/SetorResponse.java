package com.ifrn.chamados.dto.response;

import com.ifrn.chamados.model.Setor;

public record SetorResponse(
        Long id,
        String nome,
        String sigla
) {
    public static SetorResponse fromEntity(Setor setor) {
        return new SetorResponse(setor.getId(), setor.getNome(), setor.getSigla());
    }
}
