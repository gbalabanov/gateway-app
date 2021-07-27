FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=target/gateway-app-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "application.jar", "--gateway.broker.queues=${GATEWAY_BROKER_QUEUES}"]
