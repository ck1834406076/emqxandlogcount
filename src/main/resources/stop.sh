#!/bin/bash

pids=`ps -ef | grep kfkToExcel.jar | grep -v grep | awk '{print $2}'`
if [ -n "$pids" ]; then
        echo "$pids"
        kill -9 $pids
fi