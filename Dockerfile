# =============================================================================
# Proyecto : Asesor Turistico Ferroviario y Peatonal - MTC
# Curso    : Ingenieria de Software - UNU
#
# Contenedor de aplicacion de la arquitectura fisica (seccion 5.2 del
# documento): Spring Boot 4.1.1 sobre Java 17 con Tomcat embebido,
# publicado en el puerto no estandar 8082.
#
# Construccion en dos etapas para que la imagen final no arrastre Maven
# ni el codigo fuente.
# =============================================================================

# --- Etapa 1: compilacion --------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Las dependencias se resuelven primero para aprovechar la cache de capas
# cuando solo cambia el codigo fuente.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# --- Etapa 2: ejecucion ----------------------------------------------------
FROM eclipse-temurin:17-jre
WORKDIR /app

# curl: lo usa el healthcheck de docker-compose contra /actuator/health.
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# La aplicacion no necesita privilegios de root dentro del contenedor.
RUN useradd --system --create-home --shell /usr/sbin/nologin mtc
USER mtc

COPY --from=build /build/target/asesor-turistico-mtc-*.jar app.jar

ENV TZ=America/Lima
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
