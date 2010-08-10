#!/bin/sh


export LD_LIBRARY_PATH=`pwd`:$LD_LIBRARY_PATH
java -cp xbow-base-1.0-SNAPSHOT-jar-with-dependencies.jar agh.msc.xbowbase.App
