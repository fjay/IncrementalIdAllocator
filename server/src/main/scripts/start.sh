#!/bin/sh

cd `dirname $0`

nohup java -Dfile.encoding=UTF-8 -jar db-executor-server-*.jar &

cd -