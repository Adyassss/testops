#!/bin/bash

set -e  # падать при ошибке

# ====== НАСТРОЙКИ ======
IMAGE_NAME="nbank-tests"
DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME:-"zikls"}
TAG=${TAG:-"latest"}

# ====== ПРОВЕРКА ТОКЕНА ======
if [ -z "$DOCKERHUB_TOKEN" ]; then
  echo "❌ Ошибка: переменная DOCKERHUB_TOKEN не задана"
  echo "Сделай так:"
  echo "export DOCKERHUB_TOKEN=your_token"
  exit 1
fi

# ====== СБОРКА ОБРАЗА ======
echo ">>> Сборка Docker образа"
docker build -t $IMAGE_NAME .

# ====== ЛОГИН В DOCKER HUB ======
echo ">>> Логин в Docker Hub"
echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin

# ====== ТЕГИРОВАНИЕ ======
FULL_IMAGE_NAME="$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"

echo ">>> Тегирование образа: $FULL_IMAGE_NAME"
docker tag $IMAGE_NAME $FULL_IMAGE_NAME

# ====== PUSH ======
echo ">>> Отправка образа в Docker Hub"
docker push $FULL_IMAGE_NAME

# ====== ГОТОВО ======
echo ">>> Готово 🎉"
echo "Скачать образ можно так:"
echo "docker pull $FULL_IMAGE_NAME"