#!/usr/bin/env bash
# =====================================================================
# Smoke test pos-deploy. Valida que o ambiente esta de pe e respondendo.
# Uso: ./scripts/smoke-test.sh staging|production
# =====================================================================
set -euo pipefail

ENVIRONMENT="${1:?uso: smoke-test.sh staging|production}"
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
ENV_FILE="$ROOT_DIR/.env.${ENVIRONMENT}"
set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

BASE_URL="http://localhost:${API_PORT}"
AUTH="${API_ADMIN_USER}:${API_ADMIN_PASSWORD}"
FALHAS=0

checar() {  # descricao, status esperado, comando curl...
  local desc="$1" esperado="$2"; shift 2
  local status
  status="$(curl -s -o /tmp/smoke_body -w '%{http_code}' "$@" || true)"
  if [[ "$status" == "$esperado" ]]; then
    printf '  [OK]   %-45s HTTP %s\n' "$desc" "$status"
  else
    printf '  [FALHA] %-44s HTTP %s (esperado %s)\n' "$desc" "$status" "$esperado"
    FALHAS=$((FALHAS + 1))
  fi
}

echo ">> Smoke test em ${ENVIRONMENT} (${BASE_URL})"

for i in $(seq 1 60); do
  if curl -fs "${BASE_URL}/actuator/health" | grep -q '"UP"'; then break; fi
  echo "   aguardando API... ($i/60)"; sleep 5
done

checar "GET  /actuator/health"            200 "${BASE_URL}/actuator/health"
checar "GET  /actuator/info"              200 "${BASE_URL}/actuator/info"
if grep -q "\"environment\":\"${ENVIRONMENT}\"" /tmp/smoke_body; then
  echo "  [OK]   ambiente reportado pela API = ${ENVIRONMENT}"
else
  echo "  [FALHA] ambiente reportado diferente de ${ENVIRONMENT}"; FALHAS=$((FALHAS + 1))
fi
checar "GET  /api/empresas"               200 "${BASE_URL}/api/empresas"
checar "GET  /api/tipos-transporte"       200 "${BASE_URL}/api/tipos-transporte"
checar "GET  /api/deslocamentos"          200 "${BASE_URL}/api/deslocamentos"
checar "GET  /swagger-ui/index.html"      200 "${BASE_URL}/swagger-ui/index.html"
checar "POST /api/empresas sem auth"      401 -X POST -H 'Content-Type: application/json' -d '{}' "${BASE_URL}/api/empresas"

CNPJ="99.$(date +%s | tail -c 4).000/0001-$(shuf -i 10-99 -n 1)"
checar "POST /api/empresas com auth"      201 -u "$AUTH" -X POST -H 'Content-Type: application/json' \
  -d "{\"nome\":\"Smoke Test ${ENVIRONMENT}\",\"cnpj\":\"${CNPJ}\",\"setor\":\"Teste\",\"pontosSustentaveis\":1}" \
  "${BASE_URL}/api/empresas"
NOVO_ID="$(grep -o '"id":[0-9]*' /tmp/smoke_body | head -1 | cut -d: -f2 || true)"
if [[ -n "${NOVO_ID}" ]]; then
  checar "DELETE /api/empresas/${NOVO_ID} (limpeza)" 204 -u "$AUTH" -X DELETE "${BASE_URL}/api/empresas/${NOVO_ID}"
fi

if [[ "$FALHAS" -gt 0 ]]; then
  echo ">> Smoke test FALHOU ($FALHAS verificacao(oes))"; exit 1
fi
echo ">> Smoke test OK em ${ENVIRONMENT}"
