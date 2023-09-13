#!/usr/bin/env bash

set -ex

BASE=$(dirname $0)/..

mvn --file "${BASE}/annotations/pom.xml" clean gpg:sign deploy
