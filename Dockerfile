FROM gradle:6.8.0-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11-jdk-slim
COPY --from=build /home/gradle/src/build/libs/*.jar /app/fluffy.jar
ENTRYPOINT ["java","-jar","/app/fluffy.jar"]