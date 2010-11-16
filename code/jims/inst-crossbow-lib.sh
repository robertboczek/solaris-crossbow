#!/bin/sh

JIMS_HOME=`pwd`
JIMS_CROSSBOW_LIBS=${JIMS_HOME}/jims-crossbow/jims-crossbow-native/jims-crossbow-native-lib

JAR_LIST="etherstub flow link"

echo "Starting copying crossbow libraries"

for file in $JAR_LIST 
do
	libfile=$JIMS_CROSSBOW_LIBS/jims-crossbow-native-lib-${file}/target/nar/jims-crossbow-native-lib-${file}-1.0.0-x86-SunOS-gcc-shared/lib/x86-SunOS-gcc/shared/libjims-crossbow-native-lib-${file}-3.0.0.so

	echo "Copying file ${libfile}"

	if [ -e $libfile ]
	then
		echo "File libjims-crossbow-native-lib-${file}-1.0.0.so has been copied to /usr/lib/"
		cp $libfile "/usr/lib/lib${file}-1.0.0.so"
	else
		echo "File libjims-crossbow-native-lib-${file}-1.0.0.so does not exist"
	fi
done

echo "Crossbow libraries have been copied"





