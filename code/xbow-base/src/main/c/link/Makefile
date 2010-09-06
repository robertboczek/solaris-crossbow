# Load build-environment-specific variables.
#
# INCDIR  passed to $(CC) with -I option
# LIBDIR  passed to linker with -L option


CC=gcc
CFLAGS=-std=c99 -pedantic \
       -Wall -Wno-missing-braces -Wno-unknown-pragmas \
       -D__EXTENSIONS__

LDFLAGS=-Wl,-R.
LIBS=-ldladm -lsocket -ldlpi
BASENAME=link_wrapper
LIBFNAME=lib$(BASENAME).so

INCDIR += -I..

%.o: %.c
	$(CC) $(CFLAGS) -I$(INCDIR) -fPIC -c $<

mappings.o: ../common/mappings.c
	$(CC) $(CFLAGS) -I$(INCDIR) -fPIC -c ../common/mappings.c ../common/mappings.h

all: $(LIBFNAME) test

$(LIBFNAME): link.o memory.o functor.o mappings.o ifconfig.o ip.o
	$(CC) $(CFLAGS) -fPIC -shared -o $(LIBFNAME) $^ $(LIBS)

test: test.c $(LIBFNAME)
	$(CC) $(CFLAGS) $(LDFLAGS) -L$(LIBDIR) -I$(INCDIR) -o test test.c $(LIBS) -l$(BASENAME)

clean:
	rm *.o *.so

