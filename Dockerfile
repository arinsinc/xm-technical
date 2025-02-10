FROM amazoncorretto:21.0.3-alpine
ENV APP_HOME=/usr/crypto
WORKDIR $APP_HOME
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]