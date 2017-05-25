#!/usr/bin/env bash

echo "Deploying code signing key..."

cd ./.travis || exit

openssl aes-256-cbc -K "$encrypted_1b2eb614f966_key" -iv "$encrypted_1b2eb614f966_iv" -in codesigning.asc.enc -out codesigning.asc -d
gpg --fast-import codesigning.asc

cd ../ || exit
