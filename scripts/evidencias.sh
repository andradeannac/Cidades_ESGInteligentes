#!/usr/bin/env bash
# Coleta evidencias de um ambiente (status dos containers, health, info,
# amostra de dados e logs) para a pasta evidencias-<ambiente>/ e para o
# resumo do job no GitHub Actions.
set -uo pipefail

ENVIRONMENT="${1:?uso: evidencias.sh staging|production}"
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR" || exit 1
ENV_FILE=".env.${ENVIRONMENT}"
case "$ENVIRONMENT" in
  staging)    OVERRIDE=docker-compose.staging.yml ;;
  production) OVERRIDE=docker-compose.prod.yml ;;
esac
PORT="$(grep -E '^API_PORT=' "$ENV_FILE" | cut -d= -f2)"
OUT="evidencias-${ENVIRONMENT}"
mkdir -p "$OUT"
COMPOSE=(docker compose --env-file "$ENV_FILE" -f docker-compose.yml -f "$OVERRIDE")

"${COMPOSE[@]}" ps                       > "$OUT/docker-compose-ps.txt" 2>&1
curl -s "http://localhost:${PORT}/actuator/health" > "$OUT/health.json"
curl -s "http://localhost:${PORT}/actuator/info"   > "$OUT/info.json"
curl -s "http://localhost:${PORT}/api/empresas"    > "$OUT/empresas.json"
curl -s "http://localhost:${PORT}/api/deslocamentos" > "$OUT/deslocamentos.json"
"${COMPOSE[@]}" logs --no-color --tail 300 api > "$OUT/api.log" 2>&1
cp "smoke-${ENVIRONMENT}.txt" "$OUT/" 2>/dev/null || true

if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
  {
    echo "## Ambiente: ${ENVIRONMENT^^}"
    echo "**Imagem:** \`${APP_IMAGE:-n/a}\`"
    echo; echo "### Containers"; echo '```'; cat "$OUT/docker-compose-ps.txt"; echo '```'
    echo "### /actuator/health"; echo '```json'; cat "$OUT/health.json"; echo; echo '```'
    echo "### /actuator/info"; echo '```json'; cat "$OUT/info.json"; echo; echo '```'
    echo "### Smoke test"; echo '```'; cat "$OUT/smoke-${ENVIRONMENT}.txt" 2>/dev/null; echo '```'
    echo "### GET /api/empresas"; echo '```json'; head -c 1500 "$OUT/empresas.json"; echo; echo '```'
  } >> "$GITHUB_STEP_SUMMARY"
fi
echo ">> Evidencias salvas em $OUT/"
