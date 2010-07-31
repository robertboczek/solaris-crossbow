#include <stdio.h>

#include "flowadm_wrapper.h"


int main( int agrc, char** argv )
{
	char** names;

	init();

	#if 0
	names = get_names();

	while ( *names != 0 )
	{
		printf( "%s\n", *names );
		++names;
	}

	{
		char prop[] = "maxbw";
		char* values[] = { "1500000" };

		set_property( "flow", prop,
		              values, sizeof( values ) / sizeof( values[ 0 ] ), 0 );
	}

	{
		get_properties( "flow" );
	}
	#endif

	{
		printf( "RESET PROPERTY START\n" );

		printf( "%d\n",
		reset_property( "nowy", "priority", 0 ) );

		printf( "RESET PROPERTY END\n" );
	}

	{
		flow_info_t arg;

		create( &arg );
	}

	{
		char* links[] = { "e1000g0" };
		int len;

		get_flows_info( links, &len );
	}

	return 0;
}

