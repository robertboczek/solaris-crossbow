# Load build-environment-specific variables.
#
# INCDIR  passed to $(CC) with -I option
# LIBDIR  passed to linker with -L option

include Makefile.defs


CC=gcc
CFLAGS=-std=c99 -Wall -pedantic \
       -g
LDFLAGS=-Wl,-R.
LIBS=-ldladm -lsocket
BASENAME=etherstub_wrapper
LIBFNAME=lib$(BASENAME).so

%.o: %.c
	$(CC) $(CFLAGS) -I$(INCDIR) -fPIC -c $<

mappings.o: ../common/mappings.c
	$(CC) $(CFLAGS) -I$(INCDIR) -fPIC -c ../common/mappings.c ../common/mappings.h

all: $(LIBFNAME) test

$(LIBFNAME): etherstub.o memory.o mappings.o
	$(CC) $(CFLAGS) -fPIC -shared -o $(LIBFNAME) $^ $(LIBS)

test: test.c $(LIBFNAME)
	$(CC) $(CFLAGS) $(LDFLAGS) -I$(INCDIR) -L$(LIBDIR) -o test test.c $(LIBS) -l$(BASENAME)

clean:
	rm *.o *.so

