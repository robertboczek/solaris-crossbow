#!/bin/sh


INFOFILE=infofile
OUTPUT_DIR=target/site/lcov


# We don't want coverage reports for tests.

rm `find target | egrep "test.*\.(gcda|gcno)"`

lcov -c -d . -o $INFOFILE
genhtml -o $OUTPUT_DIR $INFOFILE
rm $INFOFILE

