#!/bin/bash
javac -d bin -cp .:src src/General/$1.java &&
echo "Everything fine."
