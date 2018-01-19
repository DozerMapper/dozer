#!/usr/bin/env bash

if [[ "${TRAVIS_BRANCH}" = "master" ]] && [[ "${TRAVIS_PULL_REQUEST}" = "false" ]];
then
  echo "About to deploy..."
  ./mvnw deploy -B -Prelease-ossrh -DskipTests --settings .travis/deploy-settings.xml
fi
