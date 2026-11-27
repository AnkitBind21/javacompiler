#!/bin/sh
# Compile the String module + tests and run them. Needs a JDK (javac).
set -e
cd "$(dirname "$0")"
rm -rf out && mkdir out
javac -d out $(find src test -name '*.java')
java -cp out compiler.str.StrTests
