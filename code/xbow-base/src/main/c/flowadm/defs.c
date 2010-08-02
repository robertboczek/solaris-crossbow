#include <libdlflow.h>

#include "defs.h"


int map_status( int dladm_status )
{
	// TODO-DAWID: refactor (array)

	if ( DLADM_STATUS_OK == dladm_status )
	{
		return XBOW_STATUS_OK;
	}
	else if ( DLADM_STATUS_PROP_PARSE_ERR == dladm_status )
	{
		return XBOW_STATUS_PROP_PARSE_ERR;
	}
	else
	{
		return XBOW_STATUS_UNKNOWN_ERR;
	}
}

