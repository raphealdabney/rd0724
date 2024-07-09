FROM eclipse-temurin:17-jdk-alpine
MAINTAINER baeldung.com
COPY target/ToolRentalPos-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.properties /application.properties
EXPOSE 8080
#ENTRYPOINT ["sh", "-c", "java -jar /app.jar -Dspring.config.location=/application.properties"]
#ENTRYPOINT ["sh", "-c", "java --version"]
ENTRYPOINT ["java", "-jar", "/app.jar"]
