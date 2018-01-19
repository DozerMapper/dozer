#!/usr/bin/env bash

rm -rf ././../../DozerMapper-github-io/gitbook/
rm -rf ././../../DozerMapper-github-io/dtd/
rm -rf ././../../DozerMapper-github-io/schema/

./build-gitbook.sh

mv gitbook ././../../DozerMapper-github-io/gitbook/
mv user-guide.pdf ././../../DozerMapper-github-io/user-guide.pdf

cp -R ././../schema/src/main/resources/dtd/ ././../../DozerMapper-github-io/dtd
cp -R ././../schema/src/main/resources/schema/ ././../../DozerMapper-github-io/schema
