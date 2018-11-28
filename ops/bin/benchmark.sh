#!/bin/bash

echo "Starting ... "

nmon -s1 -c80 -f -t
echo "==> nmon profiling is started"
echo "==> prepare to run wrk"
sleep 5
cd /opt/wrk
./wrk -t 5 -c 5 -s ./scripts/calculator.lua -d 60s --latency http://localhost/api/v1/compute
echo "==> cleanup"
sleep 15
echo "Finished"
