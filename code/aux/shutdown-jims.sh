#!/bin/sh

kill -s 9 `jps | grep JIMSAgent | cut -d " " -f 1`

