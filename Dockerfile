# syntax=docker/dockerfile:1.7
# =====================================================================
# Cidades ESG Inteligentes - Mobilidade Sustentavel API
# Build multi-stage: (1) compila com Maven  (2) extrai camadas  (3) runtime JRE
# =====================================================================

# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Baixa dependencias primeiro (camada cacheada enquanto o pom.xml nao mudar)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline

# Compila o codigo. Os testes rodam no pipeline (job "build-test"),
# por isso aqui sao pulados para nao duplicar tempo de build.
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests package

# Separa o fat-jar em camadas (dependencias mudam pouco, codigo muda muito)
RUN java -Djarmode=tools -jar target/app.jar extract --layers --launcher --destination /workspace/extracted

# ---------- Etapa 2: runtime ----------
FROM eclipse-temurin:17-jre-jammy AS runtime

LABEL org.opencontainers.image.title="cidades-esg-inteligentes-api" \
      org.opencontainers.image.description="API ESG de Mobilidade Sustentavel (Spring Boot + Oracle)" \
      org.opencontainers.image.licenses="MIT"

# Usuario sem privilegios de root
RUN groupadd --system spring && useradd --system --gid spring --home /app spring \
    && mkdir -p /app/logs && chown -R spring:spring /app
WORKDIR /app

# Copia as camadas da menos para a mais volatil (melhor reaproveitamento de cache)
COPY --from=build --chown=spring:spring /workspace/extracted/dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/application/ ./

USER spring

ENV SERVER_PORT=8080 \
    APP_ENV=local \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

# Health check sem dependencias extras (curl/wget nao sao necessarios)
HEALTHCHECK --interval=15s --timeout=5s --start-period=90s --retries=5 \
  CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/${SERVER_PORT} && printf "GET /actuator/health HTTP/1.0\r\nHost: localhost\r\n\r\n" >&3 && grep -q "\"status\":\"UP\"" <&3' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
