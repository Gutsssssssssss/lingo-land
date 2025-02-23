# 베이스 이미지 설정
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY land-batch-0.0.1-SNAPSHOT.jar /app/lingoland-batch.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/lingoland-batch.jar"]