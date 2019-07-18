#!/bin/bash

BUILD_VERSION=$1

echo "Using build version: $BUILD_VERSION..."

# workaround: on a travis VM, it is required to load the nvm function before using nvm/node (maven build calls npm)
source ~/.nvm/nvm.sh

echo "Setting the project version (mvn and npm) to the version: $BUILD_VERSION.."
./mvnw versions:set -DnewVersion=$BUILD_VERSION
