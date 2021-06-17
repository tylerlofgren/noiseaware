FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine
COPY build/libs/noiseaware-*-all.jar noiseaware.jar
EXPOSE 8080
CMD ["java", "-jar", "noiseaware.jar"]
