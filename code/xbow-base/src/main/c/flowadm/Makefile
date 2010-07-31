# Load build-environment-specific variables.
#
# INCDIR  passed to $(CC) with -I option
# LIBDIR  passed to linker with -L option

include Makefile.defs


CC=gcc
CFLAGS=-std=c99 -Wall -pedantic \
       -g
LDFLAGS=-Wl,-R.
LIBS=-ldladm
BASENAME=flowadm_wrapper
LIBFNAME=lib$(BASENAME).so

%.o: %.c
	$(CC) $(CFLAGS) -I$(INCDIR) -fPIC -c $<

all: $(LIBFNAME) test

$(LIBFNAME): flowadm_wrapper.o functor.o
	$(CC) $(CFLAGS) -fPIC -shared -o $(LIBFNAME) $^ $(LIBS)

test: test.c $(LIBFNAME)
	$(CC) $(CFLAGS) $(LDFLAGS) -I$(INCDIR) -L$(LIBDIR) -o test test.c $(LIBS) -l$(BASENAME)

clean:
	rm *.o *.so

