FROM alpine/java:21-jdk
RUN addgroup -S movietracker && adduser -S movietracker -G movietracker
USER movietracker:movietracker
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]