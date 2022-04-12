#! /bin/bash -eux

FIRST_RUN="mvn clean verify -DskipTests=true -Papp1profile"
SECOND_RUN="mvn clean verify -DskipTests=true -Papp2profile"

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