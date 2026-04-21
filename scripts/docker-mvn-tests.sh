#!/usr/bin/env bash
# Запуск из образа тестов: без «$$» в одной строке с CMD Docker (в bash $$ = PID).
set -euo pipefail
mkdir -p /app/logs
echo ">>> mvn test, profile=${TEST_PROFILE:?set TEST_PROFILE (e.g. api or ui)}"
mvn -B test -P "${TEST_PROFILE}" 2>&1 | tee /app/logs/run.log
echo ">>> mvn surefire-report:report"
mvn -B -DskipTests=true surefire-report:report 2>&1 | tee -a /app/logs/run.log
