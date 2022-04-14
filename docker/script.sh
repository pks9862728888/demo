#! /bin/bash -eux

# Environment variables
# MVN_VOLUME

# Application commands
FIRST_RUN="mvn clean verify -Papp1profile -DskipTests=true -Dmaven.repo.local=${MVN_VOLUME}mymaven/m2"
SECOND_RUN="mvn clean verify -Papp2profile -DskipTests=true -Dmaven.repo.local=${MVN_VOLUME}mymaven/m2"

# Change working directory
cd "${PROJECT_DIR}"

# Execute application
echo "Executing first..."
if eval "${FIRST_RUN}"; then
   echo "First app executed!"

   echo "Executing second app..."
   if eval "${SECOND_RUN}"; then
     echo "Second app executed!"
     exit 0
   else
     echo "Second app execution failed :("
     exit 1
   fi
else
  echo "First app execution failed :("
  exit 1
fi