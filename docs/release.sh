#!/usr/bin/env bash
#
# Copyright 2005-2019 Dozer Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


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
