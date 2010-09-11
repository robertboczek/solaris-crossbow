#include <libdladm.h>

#include <stdlib.h>

#include <common/defs.h>
#include <common/mappings.h>


static int mappings[][ 2 ] = {

	{ DLADM_STATUS_OK,                XBOW_STATUS_OK },
	{ DLADM_STATUS_PROP_PARSE_ERR,    XBOW_STATUS_PROP_PARSE_ERR },
	{ DLADM_STATUS_NOTFOUND,          XBOW_STATUS_NOTFOUND },
	{ DLADM_STATUS_FLOW_INCOMPATIBLE, XBOW_STATUS_FLOW_INCOMPATIBLE }

};


static int max_dladm_status = -1;
static int* internal_mappings;


int init_mapping( void )
{
	// Find the maximum status number.
	int  i;
	for ( i = 0; i < LEN( mappings ); ++i )
	{
		if ( max_dladm_status < mappings[ i ][ 0 ] )
		{
			max_dladm_status = mappings[ i ][ 0 ];
		}
	}

	// Allocate array big enough to handle all supported DLADM_STATUSes.

	internal_mappings = malloc( sizeof( *internal_mappings )
	                            * ( max_dladm_status + 1 ) );
	
	// Assume we don't know any mappings.

	for ( i = 0; i < max_dladm_status + 1; ++i )
	{
		internal_mappings[ i ] = XBOW_STATUS_UNKNOWN_ERR;
	}

	// Fill internal mappings with user-defined mappings.

	for ( i = 0; i < LEN( mappings ); ++i )
	{
		internal_mappings[ mappings[ i ][ 0 ] ] = mappings[ i ][ 1 ];
	}

	return 0;
}


int map_status( int dladm_status )
{
	int xbow_status = XBOW_STATUS_UNKNOWN_ERR;

	if ( ( 0 <= dladm_status ) && ( dladm_status <= max_dladm_status ) )
	{
		// The mapping can be read from internal_mappings array. Get it.

		xbow_status = internal_mappings[ dladm_status ];
	}

	return xbow_status;
}

