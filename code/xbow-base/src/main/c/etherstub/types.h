#ifndef ETHERTSTUB_WRAPPER_TYPES_H
#define ETHERSTUB_WRAPPER_TYPES_H

#include <stdlib.h>


typedef struct
{
	char *key, *value;
}
key_value_pair_t;

/** Possible types of etherstub parameters
 *  deliberately CLASS is omitted as its etherstub
*/
typedef enum {
	BRIDGE = 0,
	OVER,
	STATE,
	MTU
} etherstub_parameter_type_t;

/** Possible types of etherstub properties
*/
typedef enum {
	MAXBW = 0, /* bandwidth specified in megabytes - minimum bandwidth is 1.2 MB */
	LEARN_LIMIT,
	CPUS,/*@todo test how to pass two or more processors names*/
	PRIORITY
} etherstub_property_type_t;


/** Possible types of etherstub statistics */
typedef enum {
	IPACKETS = 0,
	RBYTES,
	IERRORS,
	OPACKETS,
	OBYTES,
	OERRORS
} etherstub_statistic_type_t;

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

