FROM eclipse-temurin:21-jdk AS build
WORKDIR /build
COPY . .
RUN chmod +x mvnw && ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/webMarketar-0.0.1-SNAPSHOT.jar /app/app.jar
COPY media /app/media-seed
COPY docker/entrypoint.sh /app/entrypoint.sh
RUN chmod 0755 /app/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/app/entrypoint.sh"]
