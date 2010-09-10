#ifndef ETHERTSTUB_WRAPPER_TYPES_H
#define ETHERSTUB_WRAPPER_TYPES_H

#include <stdlib.h>


typedef struct
{
	char *key, *value;
}
key_value_pair_t;

/** Struture containing array of etherstub's names and their quantity */
typedef struct{
	char **array;
	int number_of_elements;
}etherstub_names_t;

#endif

