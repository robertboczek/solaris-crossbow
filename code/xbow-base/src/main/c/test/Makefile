CC=gcc
CFLAGS=-Wall
LIBS=-ldladm
BASENAME=wrapper

wrapper:
	$(CC) $(CFLAGS) -c -fPIC $(BASENAME).c
	$(CC) $(CFLAGS) -shared -o libwrapper.so $(BASENAME).o $(LIBS)

clean:
	rm *.o *.so

