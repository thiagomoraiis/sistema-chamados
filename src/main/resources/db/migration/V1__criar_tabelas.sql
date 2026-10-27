CREATE TABLE setores (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    sigla VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    setor_id BIGINT NOT NULL,
    CONSTRAINT fk_categorias_setor FOREIGN KEY (setor_id) REFERENCES setores(id)
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    matricula VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    setor_id BIGINT,
    CONSTRAINT fk_usuarios_setor FOREIGN KEY (setor_id) REFERENCES setores(id)
);

CREATE TABLE chamados (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    local VARCHAR(255) NOT NULL,
    prioridade VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_abertura TIMESTAMP NOT NULL,
    data_fechamento TIMESTAMP,
    solucao TEXT,
    solicitante_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    tecnico_id BIGINT,
    CONSTRAINT fk_chamados_solicitante FOREIGN KEY (solicitante_id) REFERENCES usuarios(id),
    CONSTRAINT fk_chamados_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    CONSTRAINT fk_chamados_tecnico FOREIGN KEY (tecnico_id) REFERENCES usuarios(id)
);

CREATE TABLE interacoes (
    id BIGSERIAL PRIMARY KEY,
    texto TEXT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    chamado_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    CONSTRAINT fk_interacoes_chamado FOREIGN KEY (chamado_id) REFERENCES chamados(id),
    CONSTRAINT fk_interacoes_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id)
);
