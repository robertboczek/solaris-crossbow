#!/bin/sh

for LIB in etherstub flow link; do

echo $LIB

	ln -sn ../../../../../xbow-native-mock/src/main/c/mock xbow/xbow-native/xbow-native-lib/xbow-native-lib-$LIB/src/test/c/mock
	ln -sn ../../../../../xbow-native-mock/src/main/include/mock xbow/xbow-native/xbow-native-lib/xbow-native-lib-$LIB/src/test/include/mock

done

