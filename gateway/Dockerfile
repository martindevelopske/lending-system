FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

ENV MAVEN_VERSION=3.9.6
RUN apk add --no-cache curl && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar xz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn

COPY pom.xml ./
COPY discovery-service/pom.xml discovery-service/
COPY config-server/pom.xml config-server/
COPY gateway/pom.xml gateway/
COPY product-service/pom.xml product-service/
COPY customer-service/pom.xml customer-service/
COPY loan-service/pom.xml loan-service/
COPY notification-service/pom.xml notification-service/

COPY gateway/src gateway/src

RUN mvn -f pom.xml -pl gateway -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
