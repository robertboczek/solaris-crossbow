#include <stdio.h>
#include <string.h>

#include <flow/flowadm_wrapper.h>
#include <flow/memory.h>


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

		arg.attrs = malloc_key_value_pairs( 1 );
		arg.attrs->key_value_pairs_len = 1;
		strcpy( arg.attrs->key_value_pairs[ 0 ]->key, "local_ip" );
		strcpy( arg.attrs->key_value_pairs[ 0 ]->value, "6.6.6.6" );

		arg.attrs = malloc_key_value_pairs( 0 );
		arg.attrs->key_value_pairs_len = 0;

		create( &arg, 0 );
	}

	{
		char* links[] = { "e1000g1", NULL };

		get_flows_info( links );
	}

	{
		disable_accounting();
	}

	{
		get_flows_info( NULL );
	}

	return 0;
}

