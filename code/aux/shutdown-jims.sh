#!/bin/bash

kill -s 9 `jps | grep JIMSAgent | cut -d " " -f 1`

