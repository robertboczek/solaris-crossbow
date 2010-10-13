#!/bin/sh

. sh/set_lib_var

java -cp \
     xbow/xbow-mbean/target/xbow-base-1.0-SNAPSHOT-jar-with-dependencies.jar \
     agh.msc.xbowbase.App
