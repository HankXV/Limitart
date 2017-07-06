#!/bin/bash
program_name=YourJarName
kill `cat ${program_name}.pid`
tail -f ./logs/${program_name}.log