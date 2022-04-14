FROM maven:3.8.4-jdk-11
LABEL maintainer="pks9862728888"

# Copy entrypoint
COPY docker/mvn-entrypoint.sh /usr/local/scripts/mvn-entrypoint.sh
RUN chmod +x /usr/local/scripts/mvn-entrypoint.sh

ENTRYPOINT ["/usr/local/scripts/mvn-entrypoint.sh"]