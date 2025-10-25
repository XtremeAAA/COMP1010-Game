#!/bin/bash

# Create lib directory if it doesn't exist
mkdir -p lib

# Download JUnit if not already present
if [ ! -f "lib/junit-4.13.2.jar" ]; then
    echo "Downloading JUnit..."
    curl -L https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar -o lib/junit-4.13.2.jar
fi

# Download Hamcrest (JUnit dependency) if not already present
if [ ! -f "lib/hamcrest-core-1.3.jar" ]; then
    echo "Downloading Hamcrest..."
    curl -L https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar -o lib/hamcrest-core-1.3.jar
fi

# Compile the tests
echo "Compiling tests..."
javac -cp .:../source:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar *.java

# Run the tests
echo "Running tests..."
java -cp .:../source:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore TestGameEngine TestBattleScenarios 2>&1