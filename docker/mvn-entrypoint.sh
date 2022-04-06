#! /bin/sh -eu
eval "mvn clean verify -Papp1profile -DskipTests=true -Dmaven.repo.local=/usr/src/mymaven/m2 -f /usr/src/demo/pom.xml"
eval "mvn clean verify -Papp2profile -DskipTests=true -Dmaven.repo.local=/usr/src/mymaven/m2 -f /usr/src/demo/pom.xml"