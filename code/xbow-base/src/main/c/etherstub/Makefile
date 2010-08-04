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
LIBS += -ldladm -ldlpi -lkstat -lsecdb -lbsm -ldevinfo
BASENAME=etherstub_wrapper
LIBFNAME=lib$(BASENAME).so

%.o: %.c
	$(CC) $(CFLAGS) -fPIC -c $<

all: $(LIBFNAME) test

$(LIBFNAME): etherstub_wrapper.o
	$(CC) $(CFLAGS) -fPIC -shared -o $(LIBFNAME) $^ $(LIBS)

test: test.c $(LIBFNAME)
	$(CC) $(CFLAGS) $(LDFLAGS) -L$(LIBDIR) -o test test.c $(LIBS) -l$(BASENAME)

clean:
	rm *.o *.so

