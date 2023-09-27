#!/usr/bin/env bash

set -ex

BASE=$(dirname $0)/..

cd "${BASE}"

mvn --file "${BASE}/code/annotations/pom.xml" clean install -P release
