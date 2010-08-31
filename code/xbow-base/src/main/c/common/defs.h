#ifndef MAPPING_DEFS_H
#define MAPPING_DEFS_H

#define MAXKEYSIZE 256
#define MAXVALSIZE 256


#define LEN( array )  ( sizeof( array ) / sizeof( array[ 0 ] ) )

#define STATIC_CHECK( expr ) switch ( 0 ) { case 0: case expr:; }


enum
{
	XBOW_STATUS_OK,
	XBOW_STATUS_PROP_PARSE_ERR,
	XBOW_STATUS_NOTFOUND,
	XBOW_STATUS_FLOW_INCOMPATIBLE,
	XBOW_STATUS_UNKNOWN_ERR,
	XBOW_STATUS_TOO_LONG_NAME,
	XBOW_STATUS_INVALID_NAME,
	XBOW_STATUS_OPERATION_FAILURE,
	XBOW_STATUS_TOO_LONG_PARENT_NAME,
	XBOW_STATUS_INVALID_PARENT_NAME,

	XBOW_STATUS_LEN_             // auxiliary, don't use it as a return code
};

#endif

