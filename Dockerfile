FROM openjdk:11
ADD target/enrollmentzatsepinaviktoria-1.0-SNAPSHOT-spring-boot.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080
