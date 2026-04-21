#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

usage() {
  echo "Использование: $(basename "$0") [api|ui|all]"
  echo "  (без аргументов)   — по умолчанию: сначала api, затем ui (как all)"
  echo "  api                 — только REST-тесты (-P api)"
  echo "  ui                  — только UI через Selenoid (-P ui)"
  echo "  all                 — то же, что по умолчанию: api, затем ui"
  echo ""
  echo "Один профиль через переменную: TEST_PROFILE=api $0   или   TEST_PROFILE=ui $0"
  exit 0
}

case "${1:-}" in
  -h|--help|help) usage ;;
esac

# Аргумент перекрывает всё; иначе TEST_PROFILE из окружения; иначе по умолчанию all (api + ui)
PROFILE="${1:-${TEST_PROFILE:-all}}"
case "$PROFILE" in
  api|ui|all) ;;
  *)
    echo "❌ Неизвестный профиль: $PROFILE (ожидается api, ui или all)"
    echo "    Справка: $0 --help"
    exit 1
    ;;
esac

if ! command -v curl &> /dev/null; then
  echo "❌ Нужен curl (ожидание готовности backend/nginx)"
  exit 1
fi

wait_http() {
  local url=$1
  local label=$2
  local max_attempts=${3:-45}
  local i=0
  echo ">>> Ожидание готовности: $label"
  while (( i++ < max_attempts )); do
    if curl -sS --connect-timeout 2 --max-time 8 -o /dev/null "$url" 2>/dev/null; then
      echo ">>> $label отвечает"
      return 0
    fi
    printf ' .'
    sleep 2
  done
  echo ""
  echo "❌ Таймаут: $label ($url) не поднялся за $((max_attempts * 2)) с"
  exit 1
}

echo ">>> Остановка старой инфраструктуры"
docker compose down

echo ">>> Подтягиваем браузеры"

json_file="./config/browsers.json"

if ! command -v jq &> /dev/null; then
    echo ">>> jq не установлен — пропускаем pull browser-образов (не критично для Selenium standalone)"
    images=""
else
    images=$(jq -r '.. | objects | select(.image) | .image' "$json_file")
fi

if [ -n "${images:-}" ]; then
  for image in $images; do
      echo "Pulling $image..."
      docker pull "$image"
  done
fi

echo ">>> Запуск инфраструктуры"
docker compose up -d --build

# Контейнеры «Started» раньше, чем JVM/ nginx принимают трафик — без паузы тесты часто «висят» на connect
wait_http "http://127.0.0.1:4111/" "backend :4111"
wait_http "http://127.0.0.1/" "nginx :80"
wait_http "http://127.0.0.1:4444/wd/hub/status" "selenium :4444"

run_tests() {
  local p=$1
  echo ">>> Запуск тестов, Maven profile=$p (лог ниже)"
  TEST_PROFILE="$p" docker compose --profile tests run -T --rm --build tests
}

echo ">>> Запуск тестов (лог Maven ниже; первый прогон может занять долго)"
case "$PROFILE" in
  all)
    run_tests api
    run_tests ui
    echo ">>> (при all финальный surefire.html относится к последнему шагу — ui)"
    ;;
  *)
    run_tests "$PROFILE"
    ;;
esac

REPORT_HTML="$(cd "$SCRIPT_DIR/.." && pwd)/target/.docker-reports/surefire.html"
echo ">>> Готово (профиль: $PROFILE)"
echo ">>> HTML-отчёт последнего шага Docker: $REPORT_HTML"
echo "    Открой в браузере: file://$REPORT_HTML"
