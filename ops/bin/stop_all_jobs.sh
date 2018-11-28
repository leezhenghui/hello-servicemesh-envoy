#!/bin/bash

export NOMAD_ADDR=
export NOMAD_CMD=/opt/nomad/bin/nomad

echo "Stopping addsvc-job"
$NOMAD_CMD job stop addsvcJob 

echo "Stopping subsvcJob"
$NOMAD_CMD job stop subsvcJob 

echo "Stopping frontendJob"
$NOMAD_CMD job stop frontendJob 

echo "Stopping apiGatewayJob"
$NOMAD_CMD job stop apiGatewayJob

echo "Stopping hashiUI"
$NOMAD_CMD job stop hashiUIJob

echo 'wait 5s...'
sleep 5
curl --request PUT  http://localhost:4646/v1/system/gc
echo 'force GC for nomad, await for work directory cleanup...'
sleep 15
echo 'done'
