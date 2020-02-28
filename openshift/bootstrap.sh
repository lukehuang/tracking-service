#!/bin/bash

set -m

if ! whoami &> /dev/null; then
  if [ -w /etc/passwd ]; then
    echo "${USER_NAME:-dummy}:x:$(id -u):0:${USER_NAME:-openshift} user:${HOME}:/usr/sbin/nologin" >> /etc/passwd
  fi
fi

# start JHipster Registry
java $JAVA_OPTS -Dhazelcast.logging.type=slf4j \
  -server -jar /srv/jobroom/tracking-service.jar \
  --spring.profiles.active=development,swagger \
  --spring.cloud.config.uri="http://admin:admin@${ALVCH_JHIPSTER_REGISTRY_SERVICE_HOST}:${ALVCH_JHIPSTER_REGISTRY_SERVICE_PORT}/config" \
  --jhipster.registry.password=admin \
  --spring.cloud.config.label=openshift \
  --server.port=8080 \
