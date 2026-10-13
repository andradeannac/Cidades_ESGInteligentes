# Projeto - Cidades ESGInteligentes

API RESTful do tema ESG **Mobilidade Sustentável**: empresas e colaboradores registram deslocamentos sustentáveis (bicicleta, ônibus, carro elétrico), acumulam pontos pelo CO₂ evitado e resgatam recompensas.

Nesta entrega a aplicação Java Spring Boot foi adaptada para um cenário de produção com **CI/CD no GitHub Actions**, **containerização com Docker** e **orquestração com Docker Compose** em dois ambientes: **staging** e **produção**.

**Integrantes**

| RM | Nome |
|---|---|
| 565788 | Anna Clara Souza Andrade |
| 564657 | Cauã Rodrigo Sampaio |
| 564772 | Kelvin Wendell Barboza da Djalma Silva |

---

## Estrutura do projeto

```
cidades-esg-inteligentes/
├── .github/workflows/ci-cd.yml     # Pipeline CI/CD (build, testes, imagem, staging, produção)
├── Dockerfile                      # Build multi-stage da API
├── docker-compose.yml              # Orquestração base: API + Oracle, redes e volumes
├── docker-compose.override.yml     # Ajustes do ambiente local (build + porta do banco)
├── docker-compose.staging.yml      # Ajustes do ambiente de staging
├── docker-compose.prod.yml         # Ajustes do ambiente de produção
├── .env.example                    # Modelo de variáveis (local)
├── .env.staging.example            # Modelo de variáveis (staging)
├── .env.production.example         # Modelo de variáveis (produção)
├── scripts/
│   ├── deploy.sh                   # Deploy de um ambiente com Docker Compose
│   ├── smoke-test.sh               # Testes pós-deploy (health, GET, POST/DELETE, auth)
│   └── evidencias.sh               # Coleta evidências do ambiente (ps, health, logs)
├── src/main/...                    # Código-fonte Spring Boot (controllers, models, Flyway)
├── src/test/...                    # Testes automatizados (JUnit 5 + MockMvc + H2)
├── docs/                           # Documentação técnica (PDF/DOCX) e evidências
└── pom.xml
```

---

## Como executar localmente com Docker

Pré-requisitos: Docker Desktop (ou Docker Engine) com Docker Compose v2.

1. Crie o arquivo de variáveis a partir do modelo e troque as senhas:

   ```bash
   cp .env.example .env
   ```

2. Construa a imagem e suba API + Oracle:

   ```bash
   docker compose up -d --build
   ```

   O Oracle leva de 1 a 2 minutos na primeira subida. A API só inicia quando o banco está *healthy* (`depends_on: condition: service_healthy`).

3. Acompanhe o status e os logs:

   ```bash
   docker compose ps
   docker compose logs -f api
   ```

4. Acesse:

   | Recurso | URL |
   |---|---|
   | Swagger UI | http://localhost:8080/swagger-ui.html |
   | Health check | http://localhost:8080/actuator/health |
   | Info (ambiente/versão) | http://localhost:8080/actuator/info |
   | Empresas | http://localhost:8080/api/empresas |

5. Teste uma escrita (Basic Auth com o usuário/senha definidos no `.env`):

   ```bash
   curl -u admin:SUA_SENHA -X POST http://localhost:8080/api/empresas \
     -H "Content-Type: application/json" \
     -d '{"nome":"Bike Mais Sustentavel","cnpj":"11.222.333/0001-44","setor":"Mobilidade","pontosSustentaveis":100}'
   ```

6. Para derrubar o ambiente (use `-v` para apagar também os dados do banco):

   ```bash
   docker compose down
   ```

### Simulando staging e produção na sua máquina

Os mesmos scripts usados pelo pipeline podem ser executados manualmente. Cada ambiente roda como um projeto Compose separado (containers, rede e volume próprios):

```bash
docker build -t cidades-esg-api:local .

cp .env.staging.example .env.staging          # ajuste APP_IMAGE=cidades-esg-api:local e as senhas
./scripts/deploy.sh staging && ./scripts/smoke-test.sh staging        # http://localhost:8081

cp .env.production.example .env.production    # ajuste APP_IMAGE=cidades-esg-api:local e as senhas
./scripts/deploy.sh production && ./scripts/smoke-test.sh production  # http://localhost:8080
```

### Rodando sem Docker (IntelliJ / Maven)

Suba só o banco com `docker compose up -d oracle-db`, exporte as variáveis `DB_PASSWORD` e `API_ADMIN_PASSWORD` e rode `mvn spring-boot:run`. Para rodar os testes: `mvn verify` (usam H2 em memória, não precisam do Oracle).

---

## Pipeline CI/CD

**Ferramenta:** GitHub Actions — arquivo [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml).
**Registry de imagens:** GitHub Container Registry (`ghcr.io`).

```
 push na main
     │
     ▼
┌──────────────┐   ┌────────────────┐   ┌──────────────────┐   ┌─────────────────────┐
│ build-test   │──▶│ docker-image   │──▶│ deploy-staging   │──▶│ deploy-production   │
│ mvn verify   │   │ build + push   │   │ compose up       │   │ aprovação manual    │
│ 13 testes    │   │ ghcr.io:<sha>  │   │ smoke test       │   │ compose up          │
│ jar + report │   │                │   │ evidências       │   │ smoke test          │
└──────────────┘   └────────────────┘   └──────────────────┘   └─────────────────────┘
```

| Etapa | O que faz |
|---|---|
| **1. build-test** | Checkout, JDK 17 com cache do Maven, `mvn -B verify` (compila, executa os testes automatizados e empacota o `.jar`). Publica os relatórios do Surefire e o `.jar` como artefatos e escreve o resumo dos testes na página do job. |
| **2. docker-image** | Build multi-stage com Buildx e cache do GitHub Actions. Publica no `ghcr.io` com a tag imutável do commit (`<sha>`) e `latest`. Em pull requests a imagem é só construída (sem push). |
| **3. deploy-staging** | Automático. Faz login no `ghcr.io`, gera o `.env.staging` a partir dos *secrets* do environment `staging`, executa `scripts/deploy.sh staging` (`docker compose pull` + `up --wait`), roda `scripts/smoke-test.sh` e publica as evidências (containers, health, info, logs) no resumo do job e como artefato. |
| **4. deploy-production** | Só roda se staging passou. Usa **exatamente a mesma imagem** testada em staging (*build once, deploy many*). O environment `production` pode exigir **aprovação manual** (Required reviewers). Mesmo fluxo de deploy, smoke test e evidências. |

**Gatilhos:** `push` na `main` (pipeline completo), `pull_request` para a `main` (só CI), `workflow_dispatch` (execução manual).

**Testes automatizados** (`src/test`, JUnit 5 + MockMvc + H2 em modo Oracle) — 13 casos:

- contexto sobe e a carga inicial é inserida;
- `GET` públicos de empresas, usuários, deslocamentos, tipos de transporte, recompensas e resgates;
- `404` para id inexistente, `400` para payload inválido (Bean Validation, distância negativa);
- segurança: `401` sem autenticação ou com senha errada, `201` com Basic Auth;
- `/actuator/health` = `UP` e documentação OpenAPI disponível.

### Configuração no GitHub (uma vez)

1. Crie o repositório e faça o push deste projeto na branch `main`.
2. Em **Settings → Environments** crie `staging` e `production`.
3. Em cada environment cadastre os secrets: `ORACLE_PASSWORD`, `DB_PASSWORD`, `API_ADMIN_PASSWORD`.
4. Em `production`, marque **Required reviewers** para exigir aprovação antes do deploy.
5. (Opcional) Para ambientes **persistentes**, registre um *self-hosted runner* (Settings → Actions → Runners) numa máquina/VM com Docker e crie a variável de repositório `DEPLOY_RUNNER=self-hosted`. Sem essa variável, o deploy roda em runner hospedado do GitHub (ambiente efêmero, recriado a cada execução — suficiente para validar e gerar evidências).

---

## Containerização

### Dockerfile

```dockerfile
# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests package
RUN java -Djarmode=tools -jar target/app.jar extract --layers --launcher --destination /workspace/extracted

# ---------- Etapa 2: runtime ----------
FROM eclipse-temurin:17-jre-jammy AS runtime
RUN groupadd --system spring && useradd --system --gid spring --home /app spring \
    && mkdir -p /app/logs && chown -R spring:spring /app
WORKDIR /app
COPY --from=build --chown=spring:spring /workspace/extracted/dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/application/ ./
USER spring
ENV SERVER_PORT=8080 APP_ENV=local \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=5s --start-period=90s --retries=5 \
  CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/${SERVER_PORT} && printf "GET /actuator/health HTTP/1.0\r\nHost: localhost\r\n\r\n" >&3 && grep -q "\"status\":\"UP\"" <&3' || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
```

### Estratégias adotadas

- **Multi-stage build:** o Maven e o JDK ficam só na etapa de build; a imagem final leva apenas o JRE 17 e a aplicação.
- **Camadas do Spring Boot (`jarmode=tools`):** dependências, loader e código ficam em camadas separadas. Quando só o código muda, o Docker reaproveita as camadas de dependências.
- **Cache do Maven (`--mount=type=cache`)** e `dependency:go-offline` antes de copiar o `src` para acelerar rebuilds.
- **Usuário não-root (`spring`)** na imagem final.
- **HEALTHCHECK** no `/actuator/health`, sem precisar instalar `curl` na imagem. O Compose usa esse status para o `up --wait`.
- **Configuração 100% por variáveis de ambiente:** nenhuma senha fica no código ou na imagem. Os valores vêm do `.env` (local) ou dos GitHub Secrets (pipeline).
- **JVM ciente do container** (`MaxRAMPercentage`), respeitando os limites de memória do Compose.

### Orquestração (Docker Compose)

| Recurso | Uso |
|---|---|
| **Serviços** | `api` (imagem da aplicação) e `oracle-db` (`gvenzl/oracle-free:23-slim`) |
| **Redes** | `esg-backend` (`internal: true`, o banco não sai da rede Docker) e `esg-frontend` (só a API publica porta) |
| **Volumes** | `oracle-data` (dados persistentes do Oracle) e `app-logs` (arquivo de log da API) |
| **Variáveis** | `.env` / `.env.staging` / `.env.production`; obrigatórias validadas com `${VAR:?mensagem}` |
| **Dependência** | `api` só sobe quando `oracle-db` está *healthy* |
| **Ambientes** | `docker-compose.staging.yml` (porta 8081, carga de dados de exemplo) e `docker-compose.prod.yml` (porta 8080, `restart: always`, limites de CPU/memória, rotação de logs) |

---

## Prints do funcionamento

> As imagens ficam em [`docs/evidencias/`](docs/evidencias/). O roteiro de captura está em [`docs/evidencias/ROTEIRO-PRINTS.md`](docs/evidencias/ROTEIRO-PRINTS.md).
> Link da execução do pipeline: `https://github.com/<usuario>/<repositorio>/actions/runs/<id>`

**Pipeline completo (4 jobs verdes)**
![Pipeline](docs/evidencias/01-pipeline-visao-geral.png)

**Build e testes**
![Build e testes](docs/evidencias/02-build-testes.png)

**Imagem publicada no ghcr.io**
![Imagem](docs/evidencias/03-imagem-ghcr.png)

**Deploy em staging + smoke test**
![Staging](docs/evidencias/04-deploy-staging.png)

**Aprovação e deploy em produção + smoke test**
![Produção](docs/evidencias/05-deploy-producao.png)

**Ambientes funcionando (Swagger / actuator/info em staging e produção)**
![Staging funcionando](docs/evidencias/06-staging-swagger.png)
![Produção funcionando](docs/evidencias/07-producao-swagger.png)

**Validação estática dos arquivos de DevOps** — saída real em [`docs/evidencias/validacao-estatica.txt`](docs/evidencias/validacao-estatica.txt) (actionlint, hadolint, shellcheck, `docker compose config` dos três ambientes).

---

## Tecnologias utilizadas

| Camada | Tecnologias |
|---|---|
| Linguagem / framework | Java 17, Spring Boot 3.5 (Web, Data JPA, Validation, Security, Actuator) |
| Banco de dados | Oracle Database 23 Free, Flyway (migrations), Hibernate/JPA |
| Documentação da API | springdoc-openapi (Swagger UI) |
| Testes | JUnit 5, Spring Boot Test, MockMvc, Spring Security Test, H2 |
| Build | Maven 3.9 |
| Containers | Docker (multi-stage, Buildx), Docker Compose v2 |
| CI/CD | GitHub Actions, GitHub Environments + Secrets, GitHub Container Registry |
| Qualidade dos arquivos DevOps | actionlint, hadolint, shellcheck |

---

## Endpoints principais

`GET` é público; `POST`, `PUT` e `DELETE` exigem Basic Auth (`API_ADMIN_USER` / `API_ADMIN_PASSWORD`).

| Recurso | Rotas |
|---|---|
| Empresas | `/api/empresas`, `/api/empresas/{id}` |
| Usuários | `/api/usuarios`, `/api/usuarios/{id}` |
| Tipos de transporte | `/api/tipos-transporte`, `/api/tipos-transporte/{id}` |
| Deslocamentos | `/api/deslocamentos`, `/api/deslocamentos/{id}` |
| Recompensas | `/api/recompensas`, `/api/recompensas/{id}` |
| Resgates | `/api/resgates-recompensa`, `/api/resgates-recompensa/{id}` |
| Operação | `/actuator/health`, `/actuator/info`, `/swagger-ui.html` |

---

## Checklist de entrega

| Item | OK |
|---|---|
| Projeto compactado em .ZIP com estrutura organizada | ☑ |
| Dockerfile funcional | ☑ |
| docker-compose.yml ou arquivos Kubernetes | ☑ |
| Pipeline com etapas de build, teste e deploy | ☑ |
| README.md com instruções e prints | ☐ *(marcar após inserir os prints em `docs/evidencias/`)* |
| Documentação técnica com evidências (PDF ou PPT) | ☐ *(marcar após inserir os prints no documento)* |
| Deploy realizado nos ambientes staging e produção | ☐ *(marcar após a primeira execução verde do pipeline)* |
