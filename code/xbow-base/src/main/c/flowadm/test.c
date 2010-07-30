#include "flowadm_wrapper.c"


int main( int agrc, char** argv )
{
	char** names;

	init();
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

	return 0;
}

