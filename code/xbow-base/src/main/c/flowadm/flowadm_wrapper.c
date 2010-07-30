#include <libdlflow.h>
#include <libdllink.h>
#include <libdladm.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#define LEN( array )  ( sizeof( array ) / sizeof( array[ 0 ] ) )



// TODO-DAWID: replace with proper include
#define MAX_PROP_LINE  256


dladm_handle_t handle = 0;


/**
 * \brief  Initializes the library.
 *
 * \return  0        on success
 * \return  non-zero otherwise
 */
int init()
{
	return dladm_open( &handle );
}


/**
 * \brief  Removes a flow.
 *
 * \param  flow       flow name
 * \param  temporary  determines is the change temporary
 *
 * \return  0         on success
 * \return  non-zero  otherwise
 */
int remove_flow( char* flow, int temporary )
{
	return dladm_flow_remove( handle, flow, temporary, "" );
}


int count( dladm_handle_t handle, dladm_flow_attr_t* flow_attr,
           void* counter )
{
	*( ( int* ) counter ) += 1;
	return DLADM_WALK_CONTINUE;
}

static int get_attrs( dladm_handle_t handle, dladm_flow_attr_t* flow_attr,
                      void* arg )
{
	dladm_flow_attr_t** attrs = arg;

	memcpy( *attrs, flow_attr, sizeof( *flow_attr ) );
	++( *attrs );

	return DLADM_WALK_CONTINUE;
}


/**
 * \brief Retrieves flows' attributes.
 *
 * Allocates and fills *flow_attrs array with attributes
 * for flows assigned do link_name. *len is filled with flows count.
 *
 * \param  link_name
 * \param  flow_attrs
 * \param  len
 */
void collect_flow_attrs( char* link_name,
                         dladm_flow_attr_t** flow_attrs, int* len )
{
	unsigned int link_id;

	dladm_name2info( handle, link_name, &link_id, NULL, NULL, NULL );

	dladm_walk_flow( &count, handle, link_id, len, 0 );

	*flow_attrs = malloc( sizeof( dladm_flow_attr_t ) * ( *len ) );
	dladm_walk_flow( &get_attrs, handle, 1, flow_attrs, 0 );
	*flow_attrs -= *len;
}


/**
 *
 * \return  NULL-terminated array of strings containing discovered flows' names
 *
 * \warning  Caller is responsible for freeing result[ 0 ], result[ 1 ], ...
 *           as well as result itself!
 */
char** get_names()
{
	int i, len = 0;
	dladm_flow_attr_t* flow_attrs;
	char** names;

	collect_flow_attrs( "e1000g0", &flow_attrs, &len );

	names = malloc( sizeof( char* ) * ( len + 1 ) );
	names[ len ] = 0;

	for ( i = 0; i < len; ++i )
	{
		names[ i ] = malloc( MAXFLOWNAMELEN );
		memcpy( names[ i ], flow_attrs[ i ].fa_flowname, MAXFLOWNAMELEN );
	}

	free( flow_attrs );

	// TODO-DAWID: what about freeing names?

	return names;
}


int set_property( char* flow, char* key, char* values[], unsigned int values_len, int temporary )
{
	// TODO-DAWID: parse it
	//             handle temporary

	printf( "%d\n",
	dladm_set_flowprop( handle, flow, key, values, values_len, DLADM_OPT_ACTIVE, 0 ) );

	return 0;
}


typedef struct
{
	char* flow;
	char* out;
}
get_props_arg_t;


static int get_props( void* arg, const char* propname )
{
	// TODO-DAWID: no hardcoded numbers!

	char* values[ 10 ];
	int i, values_len = 10;

	get_props_arg_t* argg = arg;

	for ( i = 0; i < LEN( values ); ++i )
	{
		values[ i ] = malloc( MAX_PROP_LINE );
	}

	argg->out = malloc( LEN( values ) * MAX_PROP_LINE );
	argg->out[ 0 ] = '\0';

	dladm_get_flowprop( handle, argg->flow, DLADM_PROP_VAL_CURRENT,
	                    propname, values, &values_len );

	for ( i = 0; i < values_len; ++i )
	{
		strcat( argg->out, values[ i ] );
		strcat( argg->out, " " );

		free( values[ i ] );

		// printf( "PROP %s\n", values[ i ] );
	}

	argg->out[ strlen( argg->out ) - 1 ] = '\0';

	printf( "%s\n", argg->out );

	return DLADM_WALK_CONTINUE;
}


#if 1
typedef struct
{
	char *key, *value;
}
key_value_pair_t;
#endif


key_value_pair_t* get_properties( char* flow )
{
	get_props_arg_t arg;
	arg.flow = flow;

	dladm_walk_flowprop( &get_props, flow, &arg );

	// TODO-DAWID: memory leaks!

	key_value_pair_t* res = malloc( sizeof( key_value_pair_t ) );

	res->key = "props";
	res->value = arg.out;

	return res;
}

