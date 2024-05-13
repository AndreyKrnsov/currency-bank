FROM maven:3.8.3-openjdk-17 AS build
WORKDIR ./home/app/
COPY src ./src
COPY pom.xml .
RUN mvn -f ./pom.xml clean package
EXPOSE 8443
ENTRYPOINT ["java","-jar","target/currency-bank-0.0.1-SNAPSHOT.jar"]

