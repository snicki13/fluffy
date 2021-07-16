FROM openjdk:14
VOLUME /tmp
ADD build/libs/fluffy-1.0-SNAPSHOT.jar fluffy.jar
ENTRYPOINT ["java","-jar","fluffy.jar"]
