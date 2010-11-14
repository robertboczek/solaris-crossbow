#!/bin/sh


# Remove existsing symlinks.

find . -type l -name mock | xargs rm -f

# Create new ones.

for LIB in etherstub flow link; do

	for SUBDIR in c include; do

		ln -sn ../../../../../jims-crossbow-native-mock/src/main/$SUBDIR/mock \
		       jims/jims-crossbow/jims-crossbow-native/jims-crossbow-native-lib/jims-crossbow-native-lib-$LIB/src/test/$SUBDIR/mock

	done

done

