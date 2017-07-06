#!/bin/bash
program_name=YourJarName
kill `cat ${program_name}.pid`
tail -f nohup.txt