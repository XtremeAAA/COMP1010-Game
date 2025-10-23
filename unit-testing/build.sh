#!/bin/bash

# Change to the script's directory
cd "$(dirname "$0")"

# Compile all test files
echo "Compiling test files..."
javac -cp "../source:../lib/*" *.java

if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

echo "Compilation successful."

# Run each test class
echo "Running TestSkill..."
java -cp ".:../source:../lib/*" org.junit.runner.JUnitCore TestSkill

echo "Running TestTeam..."
java -cp ".:../source:../lib/*" org.junit.runner.JUnitCore TestTeam

echo "Running TestCharacterClass..."
java -cp ".:../source:../lib/*" org.junit.runner.JUnitCore TestCharacterClass

echo "Running TestGameDataLoader..."
java -cp ".:../source:../lib/*" org.junit.runner.JUnitCore TestGameDataLoader

echo "All tests completed."
