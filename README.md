# Sistema de Chamados - API REST

API REST para sistema de chamados de suporte do IFRN, desenvolvida com Spring Boot, Spring Data JPA e PostgreSQL.

## Tecnologias

- Java 21
- Spring Boot 3.4.1
- Spring Data JPA
- PostgreSQL 16
- Flyway (migrações de banco)
- Lombok
- Docker e Docker Compose

## Como executar

### Com Docker (recomendado)

```bash
docker compose up
```

A API ficará disponível em `http://localhost:8080`.

### Sem Docker

1. Tenha o PostgreSQL rodando com um banco chamado `chamados`
2. Configure as variáveis de ambiente:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/chamados
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
```

3. Execute:

```bash
./mvnw spring-boot:run
```

## Migrações (Flyway)

O esquema do banco é versionado com Flyway. As migrações ficam em `src/main/resources/db/migration/`.

- `V1__criar_tabelas.sql` — Cria as tabelas do sistema
- `V2__inserir_dados_iniciais.sql` — Insere dados iniciais (setores, categorias e usuários)

O Hibernate opera em modo `validate`: ele apenas valida se as entidades estão compatíveis com o banco, sem criar ou alterar tabelas. Isso garante que a estrutura do banco é controlada exclusivamente pelas migrações.

O Flyway registra cada migração aplicada na tabela `flyway_schema_history`, armazenando o número da versão, nome do script e checksum. Uma migration já aplicada nunca é executada novamente. Se o arquivo for alterado, o checksum muda e o Flyway rejeita a inicialização. Por isso, correções devem ser feitas em uma nova migration (ex: `V3__corrigir_campo.sql`).

O `ddl-auto=update` não é usado em produção porque ele pode gerar alterações destrutivas ou inesperadas no schema. Com `validate` + Flyway, toda mudança é explícita, versionada e revisável.

## Entidades

| Entidade  | Descrição                                      |
|-----------|------------------------------------------------|
| Setor     | Departamento responsável (COTIC, Manutenção)   |
| Categoria | Tipo de problema, pertence a um setor          |
| Usuario   | Solicitante ou técnico                         |
| Chamado   | Ticket de suporte com ciclo de vida definido   |
| Interacao | Mensagem dentro de um chamado                  |

### Ciclo de vida do chamado

```
ABERTO → EM_ATENDIMENTO → RESOLVIDO
ABERTO → CANCELADO
```

## Exemplos de requisições

### Setores

```bash
# Criar setor
curl -X POST http://localhost:8080/setores \
  -H "Content-Type: application/json" \
  -d '{"nome": "Secretaria Acadêmica", "sigla": "SECAD"}'

# Listar setores
curl http://localhost:8080/setores?page=0&size=10

# Listar setores filtrando por nome
curl "http://localhost:8080/setores?nome=tecnologia"

# Buscar setor por id
curl http://localhost:8080/setores/1

# Atualizar setor
curl -X PUT http://localhost:8080/setores/1 \
  -H "Content-Type: application/json" \
  -d '{"nome": "COTIC Atualizado", "sigla": "COTIC"}'

# Excluir setor
curl -X DELETE http://localhost:8080/setores/1

# Listar categorias de um setor
curl http://localhost:8080/setores/1/categorias
```

### Categorias

```bash
# Criar categoria
curl -X POST http://localhost:8080/categorias \
  -H "Content-Type: application/json" \
  -d '{"nome": "Software", "descricao": "Instalação e configuração de software", "setorId": 1}'

# Listar categorias com filtros
curl "http://localhost:8080/categorias?nome=rede&setorId=1"

# Buscar categoria por id
curl http://localhost:8080/categorias/1

# Atualizar categoria
curl -X PUT http://localhost:8080/categorias/1 \
  -H "Content-Type: application/json" \
  -d '{"nome": "Rede e Wi-Fi", "descricao": "Problemas de conectividade", "setorId": 1}'

# Excluir categoria
curl -X DELETE http://localhost:8080/categorias/1
```

### Usuários

```bash
# Criar usuário solicitante
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome": "Pedro Lima", "email": "pedro.lima@ifrn.edu.br", "matricula": "20230005", "tipo": "SOLICITANTE"}'

# Criar usuário técnico
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome": "Laura Costa", "email": "laura.costa@ifrn.edu.br", "matricula": "20230006", "tipo": "TECNICO", "setorId": 1}'

# Listar usuários com filtros
curl "http://localhost:8080/usuarios?tipo=TECNICO&setorId=1"

# Buscar usuário por id
curl http://localhost:8080/usuarios/1

# Atualizar usuário
curl -X PUT http://localhost:8080/usuarios/1 \
  -H "Content-Type: application/json" \
  -d '{"nome": "Carlos Silva Jr", "email": "carlos.silva@ifrn.edu.br", "matricula": "20230001", "tipo": "TECNICO", "setorId": 1}'
```

### Chamados

```bash
# Abrir chamado (solicitanteId deve ser de um SOLICITANTE)
curl -X POST http://localhost:8080/chamados \
  -H "Content-Type: application/json" \
  -d '{"titulo": "Projetor não liga", "descricao": "O projetor da sala B3 não está funcionando", "local": "Bloco B, Sala 3", "prioridade": "ALTA", "categoriaId": 2, "solicitanteId": 3}'

# Listar chamados com filtros e paginação
curl "http://localhost:8080/chamados?status=ABERTO&setorId=1&prioridade=ALTA&page=0&size=10&sort=dataAbertura,desc"

# Detalhar chamado (com interações)
curl http://localhost:8080/chamados/1

# Listar chamados de um usuário
curl "http://localhost:8080/usuarios/3/chamados?status=ABERTO"

# Técnico assume o chamado
curl -X PATCH http://localhost:8080/chamados/1/assumir \
  -H "Content-Type: application/json" \
  -d '{"tecnicoId": 1}'

# Registrar interação
curl -X POST http://localhost:8080/chamados/1/interacoes \
  -H "Content-Type: application/json" \
  -d '{"texto": "Vou verificar o projetor hoje à tarde", "autorId": 1}'

# Técnico resolve o chamado
curl -X PATCH http://localhost:8080/chamados/1/resolver \
  -H "Content-Type: application/json" \
  -d '{"tecnicoId": 1, "solucao": "Lâmpada do projetor substituída"}'

# Solicitante cancela o chamado (apenas se ABERTO)
curl -X PATCH http://localhost:8080/chamados/1/cancelar \
  -H "Content-Type: application/json" \
  -d '{"solicitanteId": 3}'
```

## Dados iniciais

A migration V2 insere os seguintes dados:

**Setores:**
| ID | Nome | Sigla |
|----|------|-------|
| 1  | Coordenação de Tecnologia da Informação e Comunicação | COTIC |
| 2  | Manutenção e Infraestrutura | MAINT |

**Categorias:**
| ID | Nome | Setor |
|----|------|-------|
| 1  | Rede e Internet | COTIC |
| 2  | Equipamentos de TI | COTIC |
| 3  | Infraestrutura Elétrica | MAINT |
| 4  | Infraestrutura Predial | MAINT |

**Usuários:**
| ID | Nome | Tipo | Setor |
|----|------|------|-------|
| 1  | Carlos Silva | TECNICO | COTIC |
| 2  | Ana Souza | TECNICO | MAINT |
| 3  | Maria Oliveira | SOLICITANTE | — |
| 4  | João Santos | SOLICITANTE | — |
