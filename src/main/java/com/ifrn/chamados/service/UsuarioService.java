package com.ifrn.chamados.service;

import com.ifrn.chamados.dto.request.UsuarioRequest;
import com.ifrn.chamados.dto.response.UsuarioResponse;
import com.ifrn.chamados.exception.RecursoNaoEncontradoException;
import com.ifrn.chamados.exception.RegraNegocioException;
import com.ifrn.chamados.model.Setor;
import com.ifrn.chamados.model.TipoUsuario;
import com.ifrn.chamados.model.Usuario;
import com.ifrn.chamados.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SetorService setorService;

    public UsuarioService(UsuarioRepository usuarioRepository, SetorService setorService) {
        this.usuarioRepository = usuarioRepository;
        this.setorService = setorService;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe um usuário com o email: " + request.email());
        }
        Usuario usuario = new Usuario();
        preencherDados(usuario, request);
        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(String nome, TipoUsuario tipo, Long setorId, Pageable pageable) {
        return usuarioRepository.findWithFilters(nome, tipo, setorId, pageable)
                .map(UsuarioResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = findOrThrow(id);
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = findOrThrow(id);
        if (!usuario.getEmail().equals(request.email()) && usuarioRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe um usuário com o email: " + request.email());
        }
        preencherDados(usuario, request);
        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }

    Usuario findOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com id: " + id));
    }

    private void preencherDados(Usuario usuario, UsuarioRequest request) {
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setMatricula(request.matricula());
        usuario.setTipo(request.tipo());

        if (request.tipo() == TipoUsuario.TECNICO && request.setorId() != null) {
            Setor setor = setorService.findOrThrow(request.setorId());
            usuario.setSetor(setor);
        } else {
            usuario.setSetor(null);
        }
    }
}
