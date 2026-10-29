package com.ifrn.chamados.service;

import com.ifrn.chamados.dto.request.CategoriaRequest;
import com.ifrn.chamados.dto.response.CategoriaResponse;
import com.ifrn.chamados.exception.RecursoNaoEncontradoException;
import com.ifrn.chamados.exception.RegraNegocioException;
import com.ifrn.chamados.model.Categoria;
import com.ifrn.chamados.model.Setor;
import com.ifrn.chamados.repository.CategoriaRepository;
import com.ifrn.chamados.repository.ChamadoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ChamadoRepository chamadoRepository;
    private final SetorService setorService;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            ChamadoRepository chamadoRepository,
                            SetorService setorService) {
        this.categoriaRepository = categoriaRepository;
        this.chamadoRepository = chamadoRepository;
        this.setorService = setorService;
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        Setor setor = setorService.findOrThrow(request.setorId());
        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        categoria.setSetor(setor);
        return CategoriaResponse.fromEntity(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listar(String nome, Long setorId, Pageable pageable) {
        return categoriaRepository.findWithFilters(nome, setorId, pageable)
                .map(CategoriaResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listarPorSetor(Long setorId, Pageable pageable) {
        setorService.findOrThrow(setorId);
        return categoriaRepository.findBySetorId(setorId, pageable)
                .map(CategoriaResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        Categoria categoria = findOrThrow(id);
        return CategoriaResponse.fromEntity(categoria);
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = findOrThrow(id);
        Setor setor = setorService.findOrThrow(request.setorId());
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        categoria.setSetor(setor);
        return CategoriaResponse.fromEntity(categoriaRepository.save(categoria));
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = findOrThrow(id);
        if (chamadoRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException("Não é possível excluir categoria com chamados vinculados");
        }
        categoriaRepository.delete(categoria);
    }

    Categoria findOrThrow(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com id: " + id));
    }
}
