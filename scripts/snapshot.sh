#!/usr/bin/env bash

set -ex

HERE="$(dirname $0)"

cd "${HERE}/.."

mvn -f code/annotations/pom.xml clean deploy
