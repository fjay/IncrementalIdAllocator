#!/bin/sh

cd `dirname $0`

nohup java -Dfile.encoding=UTF-8 -jar incremental-id-allocator-server-*.jar &

cd -