#!/bin/sh

rm -rf bin

javac -d bin $(find . -name "*.java")

rm -rf jre

jlink --module-path bin --output jre --add-modules java.base,counting \
--verbose \
--strip-debug \
--compress 2 \
--no-header-files \
--no-man-pages