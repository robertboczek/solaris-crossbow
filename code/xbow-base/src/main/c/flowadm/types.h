#ifndef FLOWADM_WRAPPER_TYPES_H
#define FLOWADM_WRAPPER_TYPES_H


typedef struct
{
	char* flow;
	char* out;
}
get_props_arg_t;


typedef struct
{
	char *key, *value;
}
key_value_pair_t;


// TODO-DAWID: replace pointers with fixed-size arrays?
typedef struct
{
	char* name;
	char* link;
	char* attrs;
	char* props;
	int temporary;
}
flow_info_t;

#endif

