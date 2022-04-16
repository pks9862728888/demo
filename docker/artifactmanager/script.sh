#! /bin/bash -eux

# Environment variables
# MVN_VOLUME
# ACTIVE_PROFILES

# Application commands
FIRST_RUN="mvn clean install -PmanageArtifacts -Dspring.profiles.active=${ACTIVE_PROFILES} -DskipTests=true -Dmaven.repo.local=${MVN_VOLUME}mymaven/m2"

# Change working directory
cd "${PROJECT_DIR}"

# Execute application
echo "Executing first..."
if eval "${FIRST_RUN}"; then
   echo "First app executed!"
   exit 0;
else
  echo "First app execution failed :("
  exit 1
fi