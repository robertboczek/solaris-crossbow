CC=gcc
CFLAGS=-Wall -g
LIBS=-ldladm
BASENAME=flowadm_wrapper
INC=/export/home/dawid/usr/include

wrapper:
	$(CC) $(CFLAGS) -I$(INC) -fPIC -c $(BASENAME).c
	$(CC) $(CFLAGS) -fPIC -shared -o lib$(BASENAME).so $(BASENAME).o $(LIBS)

test: test.c flowadm_wrapper.c
	$(CC) $(CFLAGS) -I$(INC) -o test test.c $(LIBS)

clean:
	rm *.o *.so

