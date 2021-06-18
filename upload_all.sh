#!/bin/bash

while IFS=, read -r timestamp symbol volume temperature; do
  curl -X POST -s -H 'Content-Type: application/json' -d "{\"timestamp\": $timestamp,\"symbol\":\"$symbol\",\"volume\":$volume,\"temperature\":$temperature}" localhost:8080/messages > /dev/null
done < BackendDeveloperAssessmentSampeData.csv
