FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Gradle bootJar 결과물을 항상 이 이름으로 나오게 맞춰놓는 걸 추천
# (build.gradle: bootJar { archiveFileName = 'fhk-security-server.jar' })
ARG JAR_FILE=build/libs/fhk-security-server.jar

COPY ${JAR_FILE} /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
