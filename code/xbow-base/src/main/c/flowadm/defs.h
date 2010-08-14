#ifndef FLOWADM_WRAPPER_DEFS_H
#define FLOWADM_WRAPPER_DEFS_H


#define MAXFLOWPROPERTIESLEN 4

#define LEN( array )  ( sizeof( array ) / sizeof( array[ 0 ] ) )


enum
{
	XBOW_STATUS_OK,
	XBOW_STATUS_PROP_PARSE_ERR,
	XBOW_STATUS_NOTFOUND,
	XBOW_STATUS_UNKNOWN_ERR,
	XBOW_STATUS_LEN_             // auxiliary, don't use it as a return code
};


int map_status( int dladm_status );


#endif

