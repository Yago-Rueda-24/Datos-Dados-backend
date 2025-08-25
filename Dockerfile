FROM ubuntu:latest
LABEL authors="yago"

ENTRYPOINT ["top", "-b"]

# Imagen base con JDK 17 (puedes usar 21 si Render lo soporta)
FROM eclipse-temurin:17-jdk-alpine

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom y el código fuente
COPY . .

# Construir el JAR con Maven Wrapper
RUN ./mvnw clean package -DskipTests

# Exponer el puerto (Render lo define como $PORT)
EXPOSE 8080

# Comando para ejecutar la app
CMD ["sh", "-c", "java -jar target/*.jar --server.port=$PORT"]