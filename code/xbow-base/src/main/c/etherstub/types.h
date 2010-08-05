#ifndef ETHERTSTUB_WRAPPER_TYPES_H
#define ETHERSTUB_WRAPPER_TYPES_H


typedef struct
{
	char *key, *value;
}
key_value_pair_t;

/** Possible types of etherstub properties
 *  deliberately CLASS is omitted as its etherstub
*/
typedef enum {
	BRIDGE = 1,
	OVER,
	STATE,
	MTU
} etherstub_property_type_t;

/** Possible types of etherstub statistics */
typedef enum {
	IPACKETS = 1,
	RBYTES,
	IERRORS,
	OPACKETS,
	OBYTES,
	OERRORS
} etherstub_statistic_type_t;

/** Temporary or persistent enum*/
typedef enum {
	PERSISTENT = 1,
	TEMPORARY
} persistence_type_t;

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

#endif

