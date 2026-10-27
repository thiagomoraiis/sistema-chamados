package com.ifrn.chamados.dto.response;

import com.ifrn.chamados.model.Chamado;
import com.ifrn.chamados.model.Prioridade;
import com.ifrn.chamados.model.StatusChamado;

import java.time.LocalDateTime;

public record ChamadoResponse(
        Long id,
        String titulo,
        String descricao,
        String local,
        Prioridade prioridade,
        StatusChamado status,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        String solucao,
        Long solicitanteId,
        String solicitanteNome,
        Long categoriaId,
        String categoriaNome,
        Long tecnicoId,
        String tecnicoNome
) {
    public static ChamadoResponse fromEntity(Chamado chamado) {
        return new ChamadoResponse(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getLocal(),
                chamado.getPrioridade(),
                chamado.getStatus(),
                chamado.getDataAbertura(),
                chamado.getDataFechamento(),
                chamado.getSolucao(),
                chamado.getSolicitante().getId(),
                chamado.getSolicitante().getNome(),
                chamado.getCategoria().getId(),
                chamado.getCategoria().getNome(),
                chamado.getTecnico() != null ? chamado.getTecnico().getId() : null,
                chamado.getTecnico() != null ? chamado.getTecnico().getNome() : null
        );
    }
}
