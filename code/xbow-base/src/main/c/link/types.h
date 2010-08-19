#ifndef LINK_WRAPPER_TYPES_H
#define LINK_WRAPPER_TYPES_H


typedef struct
{
	char* name;
	int up;
}
nic_info_t;


typedef struct
{
	nic_info_t** nic_infos;
	size_t nic_infos_len;
}
nic_infos_t;

/** Result type enum*/
typedef enum {
	RESULT_OK = 0,
	OPERATION_FAILURE,
	INVALID_LINK_NAME,
	TOO_LONG_LINK_NAME,
	INVALID_PARENT_LINK_NAME,
	TOO_LONG_PARENT_LINK_NAME
} link_return_type_t;

/** Struture containing array of links's names and their quantity */
typedef struct{
	char **array;
	int number_of_elements;
}link_names_t;


#endif

