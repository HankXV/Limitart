#!/bin/bash
program_name=YourJarName
rm -rf nohup.out
nohup java -Dfile.encoding=UTF-8 -server -Xmx2g -Xms2g -Xmn1g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=768m -XX:+UseConcMarkSweepGC -XX:+ParallelRefProcEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=75 -XX:+ExplicitGCInvokesConcurrent -XX:+HeapDumpOnOutOfMemoryError -Xloggc:./logs/GC.log -XX:+PrintGCDateStamps -XX:+PrintGCDetails -jar ${program_name}.jar &
echo $! > ${program_name}.pid
tail -f nohup.out