# Paso 1: Compilacion con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copia dependencias y codigo fuente
COPY pom.xml .
COPY src ./src

# Compila y empaqueta omitiendo tests
RUN mvn clean package -DskipTests

# Paso 2: Crear la imagen final ligera
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Se copia el .jar desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Se expone el puerto
EXPOSE 8082

# Se define el comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]