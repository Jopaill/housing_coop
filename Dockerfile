ARG GIT_COMMIT="undefinedGitCommit"
ARG BUILD_DATE="9999-12-31"

FROM eclipse-temurin:25-jdk-alpine

LABEL org.opencontainers.image.title="housing_coop" \
      org.opencontainers.image.description="housing_coop" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.authors="jordan.paille@mail.mcgill.ca" \
      org.opencontainers.image.url="https://github.com/Jopaill/housing_coop" \
      org.opencontainers.image.source="https://github.com/Jopaill/housing_coop" \
      org.opencontainers.image.licenses="MIT"

LABEL git.commit=$GIT_COMMIT \
      build.date=$BUILD_DATE


WORKDIR /app

RUN addgroup --system javauser && adduser -S -s /usr/sbin/nologin -G javauser javauser

COPY build/libs/*.jar app.jar
RUN chown -R javauser:javauser /app

USER javauser

EXPOSE 8080


ENTRYPOINT ["java", \
  "-XX:+UseSerialGC", \
  "-Xss256k", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]
