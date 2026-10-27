INSERT INTO setores (nome, sigla) VALUES
    ('Coordenação de Tecnologia da Informação e Comunicação', 'COTIC'),
    ('Manutenção e Infraestrutura', 'MAINT');

INSERT INTO categorias (nome, descricao, setor_id) VALUES
    ('Rede e Internet', 'Problemas com rede Wi-Fi, cabeada e acesso à internet', 1),
    ('Equipamentos de TI', 'Manutenção de computadores, projetores e periféricos', 1),
    ('Infraestrutura Elétrica', 'Problemas com instalações elétricas e iluminação', 2),
    ('Infraestrutura Predial', 'Manutenção de salas, portas, janelas e ar-condicionado', 2);

INSERT INTO usuarios (nome, email, matricula, tipo, setor_id) VALUES
    ('Carlos Silva', 'carlos.silva@ifrn.edu.br', '20230001', 'TECNICO', 1),
    ('Ana Souza', 'ana.souza@ifrn.edu.br', '20230002', 'TECNICO', 2),
    ('Maria Oliveira', 'maria.oliveira@ifrn.edu.br', '20230003', 'SOLICITANTE', NULL),
    ('João Santos', 'joao.santos@ifrn.edu.br', '20230004', 'SOLICITANTE', NULL);
