#!/bin/bash

nohup java -Xms8G -Xmx8G -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/logs -jar kfkToExcel.jar >/dev/null 2>&1 &