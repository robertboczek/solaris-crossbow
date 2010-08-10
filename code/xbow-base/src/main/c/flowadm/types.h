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


typedef struct
{
	char* name;
	char* link;
	char* attrs;
	char* props;
	int temporary;
}
flow_info_t;


typedef struct
{
	flow_info_t** flow_infos;
	size_t flow_infos_len;
}
flow_infos_t;

#endif

