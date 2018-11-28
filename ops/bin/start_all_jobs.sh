#!/bin/bash

export NOMAD_ADDR=
export NOMAD_CMD=/opt/nomad/bin/nomad

echo "Starting hashi-ui"
$NOMAD_CMD run /vagrant/deployable/hashi-ui.hcl

echo "Starting add service"
$NOMAD_CMD run /vagrant/deployable/addsvc.dev.hcl

echo "Starting subtract service"
$NOMAD_CMD run /vagrant/deployable/subsvc.dev.hcl

echo "Starting frontend service"
$NOMAD_CMD run /vagrant/deployable/frontend.dev.hcl

echo "Starting api-gateway service"
$NOMAD_CMD run /vagrant/deployable/api-gateway.dev.hcl

echo "Register sidecar proxy service"
$NOMAD_CMD run /vagrant/sidecar-register/sidecar-services.hcl

echo "Run envoy proxies"
$NOMAD_CMD run /vagrant/sidecar-register/envoy-proxies.hcl
