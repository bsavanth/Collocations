#!/usr/bin/env bash
#read inputfolder
#read outputfolder
inputfolder=$1
outputfolder=$2
rm -rf output
STARTTIME=$(date +%s)
rm -rf Main.class
javac Main.java
java Main $inputfolder $outputfolder

ENDTIME=$(date +%s)
echo "It takes $[$ENDTIME - $STARTTIME] seconds to complete this task..."


