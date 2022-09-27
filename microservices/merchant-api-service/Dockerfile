FROM openjdk:11.0.5

EXPOSE 10012

ADD ./target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]