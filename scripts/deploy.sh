#!/usr/bin/env bash
# =====================================================================
# Deploy de um ambiente (staging | production) com Docker Compose.
# Uso:  APP_IMAGE=ghcr.io/org/repo:sha ./scripts/deploy.sh staging
#
# Le o arquivo .env.<ambiente>. Se ele nao existir, cria a partir das
# variaveis de ambiente (que no pipeline vem dos GitHub Secrets).
# =====================================================================
set -euo pipefail

ENVIRONMENT="${1:?uso: deploy.sh staging|production}"
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

case "$ENVIRONMENT" in
  staging)    OVERRIDE=docker-compose.staging.yml; DEFAULT_PORT=8081; DEFAULT_SEED=true ;;
  production) OVERRIDE=docker-compose.prod.yml;    DEFAULT_PORT=8080; DEFAULT_SEED=false ;;
  *) echo "Ambiente invalido: $ENVIRONMENT" >&2; exit 1 ;;
esac

ENV_FILE=".env.${ENVIRONMENT}"

gerar_senha() { tr -dc 'A-Za-z0-9' </dev/urandom | head -c 20; }

if [[ ! -f "$ENV_FILE" ]]; then
  echo ">> Gerando $ENV_FILE a partir das variaveis de ambiente"
  if [[ -z "${DB_PASSWORD:-}" || -z "${ORACLE_PASSWORD:-}" || -z "${API_ADMIN_PASSWORD:-}" ]]; then
    echo "!! Segredos nao informados: gerando senhas aleatorias (apenas para ambiente efemero)."
  fi
  umask 077
  cat > "$ENV_FILE" <<EOV
COMPOSE_PROJECT_NAME=esg-${ENVIRONMENT}
APP_ENV=${ENVIRONMENT}
API_PORT=${API_PORT:-$DEFAULT_PORT}
APP_IMAGE=${APP_IMAGE:?APP_IMAGE nao definido}
APP_BUILD=${APP_BUILD:-manual}
ORACLE_PASSWORD=${ORACLE_PASSWORD:-$(gerar_senha)}
DB_USERNAME=${DB_USERNAME:-APP_MOBILIDADE}
DB_PASSWORD=${DB_PASSWORD:-$(gerar_senha)}
API_ADMIN_USER=${API_ADMIN_USER:-admin}
API_ADMIN_PASSWORD=${API_ADMIN_PASSWORD:-$(gerar_senha)}
APP_SEED_ENABLED=${APP_SEED_ENABLED:-$DEFAULT_SEED}
EOV
fi

COMPOSE=(docker compose --env-file "$ENV_FILE" -f docker-compose.yml -f "$OVERRIDE")

echo ">> [$ENVIRONMENT] Validando configuracao do Compose"
"${COMPOSE[@]}" config --quiet

echo ">> [$ENVIRONMENT] Baixando imagens"
"${COMPOSE[@]}" pull --ignore-pull-failures

echo ">> [$ENVIRONMENT] Subindo servicos (aguardando health checks)"
"${COMPOSE[@]}" up -d --remove-orphans --wait --wait-timeout 600

echo ">> [$ENVIRONMENT] Servicos em execucao"
"${COMPOSE[@]}" ps

PORT="$(grep -E '^API_PORT=' "$ENV_FILE" | cut -d= -f2)"
echo ">> [$ENVIRONMENT] Deploy concluido: http://localhost:${PORT}/swagger-ui.html"
