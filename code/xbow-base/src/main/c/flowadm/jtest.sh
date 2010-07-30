#!/bin/sh


export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:`pwd`
java -cp xbow-base-1.0-SNAPSHOT-jar-with-dependencies.jar agh.msc.xbowbase.App
