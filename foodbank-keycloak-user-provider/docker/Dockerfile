# Use an image that has unzip utility
ARG VERSION=26.4.7
FROM quay.io/keycloak/keycloak:$VERSION as base-keycloak-image

FROM alpine:3.23.2 AS builder
ARG VERSION=26.4.7

# Install unzip utility
RUN apk add --no-cache unzip curl

# Download library from Docker image quay.io/keycloak/keycloak
RUN mkdir -p /opt/keycloak/lib/main
COPY --from=base-keycloak-image /opt/keycloak/lib/lib/main/org.keycloak.keycloak-themes-$VERSION.jar /opt/keycloak/lib/main/org.keycloak.keycloak-themes-$VERSION.jar

# Unzip the jar file and keep only keycloak.v2 theme
RUN mkdir /themes && \
    unzip /opt/keycloak/lib/main/org.keycloak.keycloak-themes-$VERSION.jar theme/keycloak.v2/* -d /themes && \
    mv /themes/theme/keycloak.v2 /themes/foodbank

FROM quay.io/keycloak/keycloak:$VERSION

ENV AUTH_SERVER_BASE_URL=""
ENV REDIRECT_URIS=""
ENV REDIRECT_URIS_ISIS=""

ENV MYSQL_HOST=""
ENV MYSQL_PORT=""
ENV MYSQL_USER=""
ENV MYSQL_PASSWORD=""
ENV MYSQL_DATABASE=""

COPY --chmod=755 docker/entrypoint.sh /

USER keycloak

# Copy themes from builder
COPY --from=builder --chmod=755 /themes/foodbank /opt/keycloak/themes/foodbank
# Replace original theme files with local changes
COPY docker/theme/foodbank /opt/keycloak/themes/foodbank

# Install custom Foodbanks user provider
COPY target/foodbank-mysql-user-provider.jar /opt/keycloak/providers/
COPY docker/providers/mysql-connector-java-8.0.23.jar /opt/keycloak/providers/
COPY docker/quarkus.properties /opt/keycloak/conf/

# Import realm
COPY docker/realm/ /import/realm/

USER root
# seems kaniko still isn't ok without this
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
#CMD ["/opt/keycloak/bin/kc.sh", "--verbose", "start-dev", "--log-level=DEBUG"]
CMD ["/opt/keycloak/bin/kc.sh", "--verbose", "start-dev"]
