@echo off
@title Dump
set CLASSPATH=.;..\..\bin\*
java -client -Dwzpath=..\..\wz\ tools.wztosql.DumpMobSkills
pause