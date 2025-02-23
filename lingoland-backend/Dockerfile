# gradle 이미지로 빌드만
FROM gradle:8.8-jdk-focal AS build

WORKDIR /app

COPY build.gradle settings.gradle ./

# Java Toolchain 설정이 Java 21을 필요로 하므로, Java 21을 설치합니다.
RUN apt-get update && apt-get install -y openjdk-21-jdk

#RUN gradle dependencies --no-daemon

COPY . /app

# 빌드해서 jar 파일 생성
RUN gradle clean build --no-daemon -x test

# JRE 이미지로 런타임 이미지 생성
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/lingoland-backend.jar

# 프로덕션 설정 파일 복사
# COPY src/main/resources/application-prod.yml /app/config/application-prod.yml



# 환경 변수 설정
# ENV SPRING_PROFILES_ACTIVE=prod


EXPOSE 8080
ENTRYPOINT [ "java" ]
CMD [ "-jar", "lingoland-backend.jar" ]