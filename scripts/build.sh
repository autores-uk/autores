#!/usr/bin/env bash

set -e

if ! command -v podman > /dev/null 2>&1; then
  echo "ERROR: this script requires podman - see https://podman.io/"
  exit 1
fi

HERE="$(dirname $0)"

cd "${HERE}/../code"

CODE="$(pwd)"
# https://hub.docker.com/_/maven/
IMAGE="docker.io/library/maven:3.9.4-eclipse-temurin-11"

podman run -v "${CODE}:/code" -w "/code" "${IMAGE}" mvn clean install javadoc:javadoc
