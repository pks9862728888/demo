FROM maven:3.8.4-jdk-11
LABEL maintainer="pks9862728888"

# Copy entrypoint
RUN mkdir -p /usr/local/scripts
COPY docker/mvn-entrypoint.sh /usr/local/scripts/mvn-entrypoint.sh
RUN chmod +x /usr/local/scripts/mvn-entrypoint.sh

# Copy code inside docker container
RUN mkdir -p /usr/src/demo
COPY . /usr/src/demo

# Make .m2 repo to keep locally downloaded files
RUN mkdir -p /usr/src/mymaven/m2

ENTRYPOINT ["/usr/local/scripts/mvn-entrypoint.sh"]