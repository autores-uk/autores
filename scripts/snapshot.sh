#!/usr/bin/env bash

set -ex

HERE="$(dirname $0)"

cd "${HERE}/.."

mvn -f code/autores/pom.xml clean deploy
