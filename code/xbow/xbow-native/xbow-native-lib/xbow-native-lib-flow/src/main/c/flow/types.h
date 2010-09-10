#ifndef FLOWADM_WRAPPER_TYPES_H
#define FLOWADM_WRAPPER_TYPES_H


typedef struct
{
	char *key, *value;
}
key_value_pair_t;


typedef struct
{
	key_value_pair_t** key_value_pairs;
	size_t key_value_pairs_len;
}
key_value_pairs_t;


typedef struct
{
	char* flow;
	key_value_pair_t** key_value_pair_it;
}
get_props_arg_t;


typedef struct
{
	char* name;
	char* link;
	key_value_pairs_t* attrs;
	key_value_pairs_t* props;
}
flow_info_t;


typedef struct
{
	flow_info_t** flow_infos;
	size_t flow_infos_len;
}
flow_infos_t;

#endif

