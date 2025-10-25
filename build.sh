#!/bin/bash
# Simple build script (unix). Ensure lib/junit-4.13.2.jar and org.json are available on classpath.
mkdir -p source
javac -d source -cp "lib/*" $(find source -name "*.java")
echo "Compiled. To run: java -cp \"source:lib/*\" mastersofmq.MastersOfMQ"


java -cp "source:lib/*" mastersofmq.MastersOfMQ
