#!/bin/bash
program_name=YourJarName
rm -rf nohup.out
nohup java -Dfile.encoding=UTF-8 -server -Xmx1g -Xms1g -Xmn512m -XX:MaxMetaspaceSize=256m -XX:+UseConcMarkSweepGC -XX:+ParallelRefProcEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=75 -XX:+ExplicitGCInvokesConcurrent -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./oom.dump -Xloggc:./logs/GC.log -XX:+PrintGCDateStamps -XX:+PrintGCDetails -jar ${program_name}.jar &
echo $! > ${program_name}.pid
tail -f nohup.out