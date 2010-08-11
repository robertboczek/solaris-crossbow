#ifndef ETHERTSTUB_WRAPPER_TYPES_H
#define ETHERSTUB_WRAPPER_TYPES_H

#include <stdlib.h>


typedef struct
{
	char *key, *value;
}
key_value_pair_t;

/** Result type enum*/
typedef enum {
	RESULT_OK = 0,
	DELETE_FAILURE,
	INVALID_ETHERSTUB_NAME,
	TOO_LONG_ETHERSTUB_NAME,
	CREATE_FAILURE,
	LIST_ETHERSTUB_NAMES_ERROR,
	ETHERSTUB_PROPERTY_FAILURE,
	ETHERSTUB_STATS_FAILURE
} etherstub_return_type_t;

/** Struture containing array of etherstub's names and their quantity */
typedef struct{
	char **array;
	int number_of_elements;
}etherstub_names_t;

#endif

