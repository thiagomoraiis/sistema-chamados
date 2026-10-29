package com.ifrn.chamados.service;

import com.ifrn.chamados.dto.request.SetorRequest;
import com.ifrn.chamados.dto.response.SetorResponse;
import com.ifrn.chamados.exception.RecursoNaoEncontradoException;
import com.ifrn.chamados.exception.RegraNegocioException;
import com.ifrn.chamados.model.Setor;
import com.ifrn.chamados.repository.ChamadoRepository;
import com.ifrn.chamados.repository.SetorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetorService {

    private final SetorRepository setorRepository;
    private final ChamadoRepository chamadoRepository;

    public SetorService(SetorRepository setorRepository, ChamadoRepository chamadoRepository) {
        this.setorRepository = setorRepository;
        this.chamadoRepository = chamadoRepository;
    }

    @Transactional
    public SetorResponse criar(SetorRequest request) {
        Setor setor = new Setor();
        setor.setNome(request.nome());
        setor.setSigla(request.sigla());
        return SetorResponse.fromEntity(setorRepository.save(setor));
    }

    @Transactional(readOnly = true)
    public Page<SetorResponse> listar(String nome, Pageable pageable) {
        Page<Setor> page;
        if (nome != null && !nome.isBlank()) {
            page = setorRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            page = setorRepository.findAll(pageable);
        }
        return page.map(SetorResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public SetorResponse buscarPorId(Long id) {
        Setor setor = findOrThrow(id);
        return SetorResponse.fromEntity(setor);
    }

    @Transactional
    public SetorResponse atualizar(Long id, SetorRequest request) {
        Setor setor = findOrThrow(id);
        setor.setNome(request.nome());
        setor.setSigla(request.sigla());
        return SetorResponse.fromEntity(setorRepository.save(setor));
    }

    @Transactional
    public void excluir(Long id) {
        Setor setor = findOrThrow(id);
        if (chamadoRepository.existsByCategoriaSetorId(id)) {
            throw new RegraNegocioException("Não é possível excluir setor com chamados vinculados");
        }
        setorRepository.delete(setor);
    }

    Setor findOrThrow(Long id) {
        return setorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Setor não encontrado com id: " + id));
    }
}
