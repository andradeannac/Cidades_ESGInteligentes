# Roteiro de prints (evidências)

Salve cada imagem nesta pasta com o nome indicado. O README e o documento técnico já apontam para esses arquivos.

| Arquivo | Onde capturar |
|---|---|
| `01-pipeline-visao-geral.png` | GitHub → **Actions** → execução da `main` → grafo com os 4 jobs verdes (build-test → docker-image → deploy-staging → deploy-production) |
| `02-build-testes.png` | Job **Build e testes** → step *Compilar, testar e empacotar* aberto mostrando `Tests run: 13, Failures: 0` e `BUILD SUCCESS` |
| `03-imagem-ghcr.png` | Página do repositório → **Packages** → imagem com a tag do commit (`<sha>`) e `latest` |
| `04-deploy-staging.png` | Job **Deploy - Staging** → steps *Deploy com Docker Compose* e *Smoke test* com todos os `[OK]` (ou o *Summary* do job com containers, health e info) |
| `05-deploy-producao.png` | Tela de **Review deployments** aprovando `production` + job **Deploy - Producao** com smoke test `[OK]` |
| `06-staging-swagger.png` | Navegador em `http://localhost:8081/swagger-ui.html` (ou `/actuator/info` mostrando `"environment":"staging"`) |
| `07-producao-swagger.png` | Navegador em `http://localhost:8080/swagger-ui.html` (ou `/actuator/info` mostrando `"environment":"production"`) |

Dicas:

- Os prints 06 e 07 exigem ambientes persistentes: use o *self-hosted runner* (variável `DEPLOY_RUNNER=self-hosted`) **ou** rode localmente `./scripts/deploy.sh staging` e `./scripts/deploy.sh production` com a imagem publicada.
- Com runner hospedado do GitHub, o **Summary** de cada job de deploy já traz `docker compose ps`, `/actuator/health`, `/actuator/info`, smoke test e `GET /api/empresas` — um print desse resumo vale como evidência de funcionamento do ambiente.
- O artefato `evidencias-staging` / `evidencias-production` (zip na página da execução) contém os mesmos dados em arquivos, incluindo o log da API.
