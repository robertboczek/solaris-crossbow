#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "aux.h"


char* strend( char* s )
{
	return ( s + strlen( s ) );
}


char* flatten_key_value_pairs( key_value_pairs_t* kvps )
{
	size_t buffer_len = 1;  // '\0' at the end

	for ( int i = 0; i < kvps->key_value_pairs_len; ++i )
	{
		buffer_len += strlen( kvps->key_value_pairs[ i ]->key );
		buffer_len += strlen( kvps->key_value_pairs[ i ]->value );
		buffer_len += strlen( "=," );
	}

	// Allocate and fill the buffer.
	
	char* buffer = malloc( buffer_len );
	*buffer = '\0';

	for ( int i = 0; i < kvps->key_value_pairs_len; ++i )
	{
		sprintf( strend( buffer ), "%s=%s,",
		         kvps->key_value_pairs[ i ]->key,
		         kvps->key_value_pairs[ i ]->value );
	}

	// If exists, remove trailing comma.
	
	if ( buffer_len > 1 )
	{
		*( strend( buffer ) - 1 ) = '\0';
	}

	return buffer;
}

