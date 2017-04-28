#!/bin/sh

PATH="/sbin:/bin:/usr/bin:/usr/sbin"
RETVAL=0

cd `dirname $0`

# --------------- Start of configurations ---------------------
# --------------- Please configure following paramters --------
JAVA_HOME="/usr"

JAVA_OPTS="-Dfile.encoding=UTF-8 -Xmx2048m"
APP_JAR_NAME="short-link-server-*"
APP_HOME=`pwd`
APP_PIDFILE=./pid.txt

# ----------------- Configuration section is over --------------.

JAVACMD="${JAVA_HOME}/bin/java"
APP_RUN_CMD="${JAVACMD} ${JAVA_OPTS} -jar ${APP_JAR_NAME}.jar"

start() {
    # Check if the service is already running. We look only for the pid file
    pid=`cat $APP_PIDFILE 2>&1`
    if [ "$?" = "0" ]; then
        echo "application is already running. Remove $APP_PIDFILE if you know this to be untrue."
        RETVAL=1
        return
    fi

    # Start application service.
    echo -n "Starting application: ${APP_RUN_CMD}"

	rm -f nohup.out
	nohup $APP_RUN_CMD > nohup.out 2>&1 &
	RETVAL=$?

	PID=`ps aux | grep -v "grep" | grep "${APP_JAR_NAME}" | awk '{print $2}'`

	if [ $RETVAL -eq 0 -a ! -z "$PID" -a ! -z "$APP_PIDFILE" ]; then
		echo $PID > $APP_PIDFILE
	fi

	echo

#	[ $RETVAL -eq 0 -a -d /var/lock/subsys ] && touch /var/lock/subsys/lsa

	sleep 1 # allows prompt to return
}

stop() {
	# Stop APP.
	echo -n "Shutting down application"

	[ -f "$APP_PIDFILE" ] && kill `cat $APP_PIDFILE`
	RETVAL=$?
	echo

	[ $RETVAL -eq 0 -a -f "$APP_PIDFILE" ] && rm -f $APP_PIDFILE
#	[ $RETVAL -eq 0 -a -f "/var/lock/subsys/lsa" ] && rm -f /var/lock/subsys/lsa
}

restart() {
	stop
	sleep 10
	start
}

status() {
    pid=`cat $APP_PIDFILE 2>&1`
	if [ "$?" = "1" ]; then
		echo "APP is not running..."
		RETVAL=0
	else
		ps -p $pid > /dev/null 2>&1
		if [ "$?" = "0" ]; then
			echo "APP is running....."
			RETVAL=0
		else
			echo "APP is not running...."
			RETVAL=0
		fi
	fi

}


# Handle how we were called.
case "$1" in
	start)
		start
		;;
	stop)
		stop
		;;
	restart)
		restart
		;;
	status)
		status
		;;
	*)
		echo "Usage $0 {start|stop|restart|status}"
		RETVAL=1
esac

cd -

exit $RETVAL