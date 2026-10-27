package com.ifrn.chamados.dto.response;

import com.ifrn.chamados.model.Categoria;

public record CategoriaResponse(
        Long id,
        String nome,
        String descricao,
        Long setorId,
        String setorNome
) {
    public static CategoriaResponse fromEntity(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getSetor().getId(),
                categoria.getSetor().getNome()
        );
    }
}
