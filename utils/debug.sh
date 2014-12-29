#!/bin/bash
PROJECT_FOLDER=$1
PROJECT_STRING=verbiste
PACKAGE=com.allansimon.verbisteandroid
ACTIVITY=MainActivity

LOCAL_PORT=`expr $RANDOM % 65536`

adb shell am start -D -n $PACKAGE/${PACKAGE}.$ACTIVITY
sleep 1
APK_PID=$(adb shell ps | grep $PROJECT_STRING | awk '{print $2}')

echo "forwarding tcp:$LOCAL_PORT to jwdp:$APK_PID"

adb forward tcp:"$LOCAL_PORT" jdwp:"$APK_PID"
jdb -sourcepath $PROJECT_FOLDER/src/ -attach localhost:$LOCAL_PORT
