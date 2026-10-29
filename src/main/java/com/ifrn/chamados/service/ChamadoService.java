package com.ifrn.chamados.service;

import com.ifrn.chamados.dto.request.*;
import com.ifrn.chamados.dto.response.ChamadoDetalhadoResponse;
import com.ifrn.chamados.dto.response.ChamadoResponse;
import com.ifrn.chamados.dto.response.InteracaoResponse;
import com.ifrn.chamados.exception.RecursoNaoEncontradoException;
import com.ifrn.chamados.exception.RegraNegocioException;
import com.ifrn.chamados.model.*;
import com.ifrn.chamados.repository.ChamadoRepository;
import com.ifrn.chamados.repository.InteracaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final InteracaoRepository interacaoRepository;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    public ChamadoService(ChamadoRepository chamadoRepository,
                          InteracaoRepository interacaoRepository,
                          UsuarioService usuarioService,
                          CategoriaService categoriaService) {
        this.chamadoRepository = chamadoRepository;
        this.interacaoRepository = interacaoRepository;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    @Transactional
    public ChamadoResponse criar(ChamadoRequest request) {
        Usuario solicitante = usuarioService.findOrThrow(request.solicitanteId());
        if (solicitante.getTipo() != TipoUsuario.SOLICITANTE) {
            throw new RegraNegocioException("Apenas usuários do tipo SOLICITANTE podem abrir chamados");
        }

        Categoria categoria = categoriaService.findOrThrow(request.categoriaId());

        Chamado chamado = new Chamado();
        chamado.setTitulo(request.titulo());
        chamado.setDescricao(request.descricao());
        chamado.setLocal(request.local());
        chamado.setPrioridade(request.prioridade() != null ? request.prioridade() : Prioridade.MEDIA);
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setDataAbertura(LocalDateTime.now());
        chamado.setSolicitante(solicitante);
        chamado.setCategoria(categoria);

        return ChamadoResponse.fromEntity(chamadoRepository.save(chamado));
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponse> listar(StatusChamado status, Prioridade prioridade,
                                         Long setorId, Long categoriaId,
                                         Long solicitanteId, Long tecnicoId,
                                         LocalDateTime dataInicio, LocalDateTime dataFim,
                                         Pageable pageable) {
        return chamadoRepository.findWithFilters(status, prioridade, setorId, categoriaId,
                        solicitanteId, tecnicoId, dataInicio, dataFim, pageable)
                .map(ChamadoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ChamadoDetalhadoResponse buscarPorId(Long id) {
        Chamado chamado = findOrThrow(id);
        return ChamadoDetalhadoResponse.fromEntity(chamado);
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponse> listarPorUsuario(Long usuarioId, StatusChamado status, Pageable pageable) {
        usuarioService.findOrThrow(usuarioId);
        return chamadoRepository.findByUsuarioId(usuarioId, status, pageable)
                .map(ChamadoResponse::fromEntity);
    }

    @Transactional
    public ChamadoResponse assumir(Long chamadoId, AssumirChamadoRequest request) {
        Chamado chamado = findOrThrow(chamadoId);

        if (chamado.getStatus() != StatusChamado.ABERTO) {
            throw new RegraNegocioException("Apenas chamados com status ABERTO podem ser assumidos");
        }

        Usuario tecnico = usuarioService.findOrThrow(request.tecnicoId());
        if (tecnico.getTipo() != TipoUsuario.TECNICO) {
            throw new RegraNegocioException("Apenas usuários do tipo TECNICO podem assumir chamados");
        }

        if (tecnico.getSetor() == null ||
                !tecnico.getSetor().getId().equals(chamado.getCategoria().getSetor().getId())) {
            throw new RegraNegocioException("O técnico só pode assumir chamados cuja categoria pertence ao seu setor");
        }

        chamado.setTecnico(tecnico);
        chamado.setStatus(StatusChamado.EM_ATENDIMENTO);

        return ChamadoResponse.fromEntity(chamadoRepository.save(chamado));
    }

    @Transactional
    public ChamadoResponse resolver(Long chamadoId, ResolverChamadoRequest request) {
        Chamado chamado = findOrThrow(chamadoId);

        if (chamado.getStatus() != StatusChamado.EM_ATENDIMENTO) {
            throw new RegraNegocioException("Apenas chamados com status EM_ATENDIMENTO podem ser resolvidos");
        }

        if (chamado.getTecnico() == null ||
                !chamado.getTecnico().getId().equals(request.tecnicoId())) {
            throw new RegraNegocioException("Somente o técnico atribuído pode resolver o chamado");
        }

        chamado.setSolucao(request.solucao());
        chamado.setStatus(StatusChamado.RESOLVIDO);
        chamado.setDataFechamento(LocalDateTime.now());

        return ChamadoResponse.fromEntity(chamadoRepository.save(chamado));
    }

    @Transactional
    public ChamadoResponse cancelar(Long chamadoId, CancelarChamadoRequest request) {
        Chamado chamado = findOrThrow(chamadoId);

        if (chamado.getStatus() != StatusChamado.ABERTO) {
            throw new RegraNegocioException("Apenas chamados com status ABERTO podem ser cancelados");
        }

        if (!chamado.getSolicitante().getId().equals(request.solicitanteId())) {
            throw new RegraNegocioException("Somente o próprio solicitante pode cancelar o chamado");
        }

        chamado.setStatus(StatusChamado.CANCELADO);
        chamado.setDataFechamento(LocalDateTime.now());

        return ChamadoResponse.fromEntity(chamadoRepository.save(chamado));
    }

    @Transactional
    public InteracaoResponse adicionarInteracao(Long chamadoId, InteracaoRequest request) {
        Chamado chamado = findOrThrow(chamadoId);

        if (chamado.getStatus() == StatusChamado.RESOLVIDO || chamado.getStatus() == StatusChamado.CANCELADO) {
            throw new RegraNegocioException("Não é possível adicionar interações em chamados RESOLVIDO ou CANCELADO");
        }

        Usuario autor = usuarioService.findOrThrow(request.autorId());

        boolean isSolicitante = chamado.getSolicitante().getId().equals(autor.getId());
        boolean isTecnico = chamado.getTecnico() != null && chamado.getTecnico().getId().equals(autor.getId());

        if (!isSolicitante && !isTecnico) {
            throw new RegraNegocioException("Apenas o solicitante e o técnico atribuído podem registrar interações");
        }

        Interacao interacao = new Interacao();
        interacao.setTexto(request.texto());
        interacao.setDataHora(LocalDateTime.now());
        interacao.setChamado(chamado);
        interacao.setAutor(autor);

        return InteracaoResponse.fromEntity(interacaoRepository.save(interacao));
    }

    private Chamado findOrThrow(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado não encontrado com id: " + id));
    }
}
