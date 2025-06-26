# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia pom.xml y descarga dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia el resto de la app y compila
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecuciónv
FROM eclipse-temurin:17-jdk

VOLUME /tmp

WORKDIR /app

COPY --from=build /app/target/credit-api-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
