FROM openjdk:17-jdk-alpine 
LABEL maintainer="jiwonpark2229@gmail.com"  
WORKDIR /app 
COPY build/libs/receipt-app-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/db/migration /app/db/migration
EXPOSE 8080 
ENTRYPOINT ["java", "-jar", "/app/app.jar"] 
