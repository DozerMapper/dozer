#!/usr/bin/env bash

rm -rf gitbook/ user-guide.pdf
gitbook build asciidoc/ gitbook/
gitbook pdf asciidoc/ user-guide.pdf
