FROM openjdk:16-jdk-alpine
VOLUME /tmp
COPY /build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]