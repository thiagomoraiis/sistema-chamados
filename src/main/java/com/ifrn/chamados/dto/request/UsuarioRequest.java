package com.ifrn.chamados.dto.request;

import com.ifrn.chamados.model.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String matricula,
        @NotNull TipoUsuario tipo,
        Long setorId
) {}
