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
	size_t len;
}
nic_infos_t;

/** Struture containing array of links's names and their quantity */
typedef struct{
	char **array;
	int number_of_elements;
}link_names_t;

typedef struct
{
	char* buffer;
	int len;
}
buffer_t;


#endif

