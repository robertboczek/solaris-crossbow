#include <stdio.h>

#include "flowadm_wrapper.h"


int main( int agrc, char** argv )
{
	init();

	#if 0
	{
		char** names;
		names = get_names();

		while ( *names != 0 )
		{
			printf( "%s\n", *names );
			++names;
		}
	}
	#endif

	{
		char prop[] = "maxbw";
		char* values[] = { "1500000" };

		set_property( "flow", prop,
		              values, sizeof( values ) / sizeof( values[ 0 ] ), 0 );
	}

	{
		get_properties( "flow" );
	}

	{
		printf( "RESET PROPERTY START\n" );

		printf( "%d\n",
		reset_property( "nowy", "priority", 0 ) );

		printf( "RESET PROPERTY END\n" );
	}

	{
		flow_info_t arg;

		arg.name = "fllow";
		arg.link = "e1000g0";
		arg.attrs = "local_ip=4.3.2.1";
		arg.props = "priority=MEDIUM";
		arg.temporary = 0;

		create( &arg );
	}

	{
		char* links[] = { "e1000g1", NULL };
		int len;

		get_flows_info( links, &len );
	}

	{
		disable_accounting();
	}

	{
		int a;
		get_flows_info( NULL, &a );

		printf( "%d\n", a );
	}

	return 0;
}

