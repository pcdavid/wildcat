#!/bin/sh
CLASSPATH='output/build:externals/antlr-2.7.6.jar:externals/jline-0.9.91-SNAPSHOT.jar'
java -cp $CLASSPATH example.shell.WildcatShell
