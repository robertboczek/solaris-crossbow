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

	#if 0
	{
		printf( "RESET PROPERTY START\n" );

		printf( "%d\n",
		reset_property( "nowy", "priority", 0 ) );

		printf( "RESET PROPERTY END\n" );
	}
	#endif

	#if 0
	{
		flow_info_t arg;

		create( &arg );
	}
	#endif

	{
		char* links[] = { "e1000g1", NULL };
		int len;

		get_flows_info( links, &len );
	}

	#if 0
	{
		disable_accounting();
	}
	#endif

	{
		int a;
		get_flows_info( NULL, &a );

		printf( "%d\n", a );
	}

	return 0;
}

