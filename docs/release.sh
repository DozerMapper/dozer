#!/usr/bin/env bash

echo "Removing old files in DozerMapper-github-io"
rm -rf .././../../DozerMapper-github-io/gitbook/
rm -rf .././../../DozerMapper-github-io/dtd/
rm -rf .././../../DozerMapper-github-io/schema/

echo "Building..."
./build-gitbook.sh

echo "Copying to DozerMapper-github-io repo..."
mv gitbook .././../../DozerMapper-github-io/gitbook/
mv user-guide.pdf .././../../DozerMapper-github-io/user-guide.pdf

cp -R ././../core/target/classes/dtd/ .././../../DozerMapper-github-io/dtd
cp -R ././../core/target/classes/schema/ .././../../DozerMapper-github-io/schema
cp -R ././../dozer-integrations/dozer-spring-support/dozer-spring4/target/classes/schema/ .././../../DozerMapper-github-io/schema
