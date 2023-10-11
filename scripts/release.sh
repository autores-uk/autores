#!/usr/bin/env bash

# Release script
# 1) Set the release version
# 2) Create release branch
# 3) Deploy to Maven central
# 4) Increment the build & set to SNAPSHOT

set -ex

BASE=$(dirname $0)/..

cd "${BASE}"

if [ -z "$(git status --porcelain)" ]; then
  echo "No uncommitted changes"
else
  echo "Uncommitted changes; resolve before continuing"
  exit 1
fi

HERE="$(pwd)"
MAJOR="$(cat ${HERE}/scripts/versions/major.txt)"
MINOR="$(cat ${HERE}/scripts/versions/minor8.txt)"
STATUS="$(cat ${HERE}/scripts/versions/status.txt)"
NEXTMINOR="$((${MINOR}+1))"

CURRENT="${MAJOR}.${MINOR}${STATUS}"
NEXT="${MAJOR}.${NEXTMINOR}${STATUS}"

BRANCH="$(git rev-parse --abbrev-ref HEAD)"
RELEASEBRANCH="release/${CURRENT}"

function upversion() {
  mvn --file "${BASE}/code/pom.xml" versions:set -DnewVersion="$1"
  mvn --file "${BASE}/code/annotations/pom.xml" versions:set -DnewVersion="$1"
}

echo "Test current state"

mvn --file "${BASE}/code/annotations/pom.xml" clean package -P release

echo "Releasing version ${CURRENT} on branch ${BRANCH}"

upversion "${NEXT}"

git add code
git commit -m "Releasing version ${CURRENT}"
git push origin "${BRANCH}"
git checkout -b "${RELEASEBRANCH}"
git push origin "${RELEASEBRANCH}"
git checkout "${BRANCH}"

mvn --file "${BASE}/code/annotations/pom.xml" clean deploy -P release

echo "${NEXTMINOR}" > "${HERE}/scripts/versions/minor8.txt"
upversion "${NEXT}-SNAPSHOT"
git add code
git add scripts/versions
git commit -m "Setting version to ${NEXT}-SNAPSHOT"
git push origin "${BRANCH}"
