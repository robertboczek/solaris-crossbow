#!/bin/sh

. sh/set_lib_var

java -cp \
     jims/jims-crossbow-mbean/target/xbow-base-1.0-SNAPSHOT-jar-with-dependencies.jar \
     org.jims.modules.crossbow.App
