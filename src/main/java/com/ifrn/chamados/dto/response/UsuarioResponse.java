package com.ifrn.chamados.dto.response;

import com.ifrn.chamados.model.TipoUsuario;
import com.ifrn.chamados.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String matricula,
        TipoUsuario tipo,
        Long setorId,
        String setorNome
) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getMatricula(),
                usuario.getTipo(),
                usuario.getSetor() != null ? usuario.getSetor().getId() : null,
                usuario.getSetor() != null ? usuario.getSetor().getNome() : null
        );
    }
}
