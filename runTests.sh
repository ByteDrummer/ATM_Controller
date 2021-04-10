#!/bin/sh
OUTPUT=testingOutput.txt

rm $OUTPUT

export CLASSPATH=.
javac *.java

for INPUT in ./tests/*
do
  echo "----------------- Result of $INPUT as input -----------------" >> $OUTPUT
  cat $INPUT | java Controller >> $OUTPUT
done
