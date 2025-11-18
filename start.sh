#!/bin/bash
cd "$(dirname "$0")"
echo "Compiling client..."
javac src/*.java
echo "Starting Bank Client..."
java -cp src BankClient

