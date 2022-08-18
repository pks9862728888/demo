#! /bin/bash -eu

SK="N"

app1cmd="mvn verify -Papp1profile"

if [ ${SK} == "Y" ] || eval "${app1cmd}"; then
  if [ ${SK} == "Y" ]; then
    echo "STAT: skipped"
  else
    echo "STAT: executed successfully"
  fi
else
  echo "STAT: execution failed"
fi
