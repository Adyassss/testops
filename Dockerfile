# базовый докер образ
# каждый раз с нуля строить базовый образ (java, mvn, git)
# голый докер образ и устанавлить java, maven
# ЕСЛИ МОЖНО СОЗДАТЬ ОБРАЗ ПОВЕРХ ДРУГОГО ОБРАЗА, ГДЕ ВСЕ УЖЕ УСТАНОВЛЕНО
# МАРКЕТПЛЕЙС ВСЕХ ДОКЕР ОБРАЗОВ - docker hub
FROM maven:3.9.9-eclipse-temurin-21

# Дефолтные значения аргументов
ARG TEST_PROFILE=api
ARG APIBASEURL=http://localhost:4111
ARG UIBASEURL=http://localhost:3000

# Переменные окружения для контейнера
ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}

# работаем из папки /app
WORKDIR /app

# копируем помник
COPY pom.xml .

# загружаем зависимости и кешируем
RUN mvn dependency:go-offline

# копируем весь проект
COPY . .

# теперь внутри есть зависимости, есть весь проект и мы готовы запускать тесты

USER root

RUN chmod +x /app/scripts/docker-mvn-tests.sh

# Скрипт вместо inline bash: в CMD с JSON «$$» не превращается в «$», bash тогда подставляет PID → profile=1{TEST_PROFILE}
CMD ["/app/scripts/docker-mvn-tests.sh"]