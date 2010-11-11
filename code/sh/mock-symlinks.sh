#!/bin/sh

for LIB in etherstub flow link; do

echo $LIB

	ln -sn ../../../../../jims-crossbow-native-mock/src/main/c/mock \
	       jims/jims-crossbow/jims-crossbow-native/jims-crossbow-native-lib/jims-crossbow-native-lib-$LIB/src/test/c/mock

	ln -sn ../../../../../jims-crossbow-native-mock/src/main/include/mock \
	       jims/jims-crossbow/jims-crossbow-native/jims-crossbow-native-lib/jims-crossbow-native-lib-$LIB/src/test/include/mock

done

