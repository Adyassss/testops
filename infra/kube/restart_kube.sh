#!/bin/bash

# ШАГ 1: поднятие сервисов приложения

# Запустили локальный Kubernetes-кластер с помощью minikube, используя Docker как драйвер
# (кластер будет запущен внутри докер контейнера)
minikube start --driver=docker

# Создали ConfigMap с именем selenoid-config, файл будет доступен под ключом browsers.json
kubectl create configmap selenoid-config --from-file=browsers.json=./nbank-chart/files/browsers.json

# Устанавливаем Helm чарт с именем релиза nbank, беря шаблоны из ./nbank-chart
# Это создаст все ресурсв, описанные в шаблонах Helm (Deployment, Service)
helm install nbank ./nbank-chart