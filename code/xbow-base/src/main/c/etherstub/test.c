#include <stdio.h>

#include "etherstub_wrapper.h"


int main( int agrc, char** argv )
{
	init();

	char name[] = "etherstub1";

	if(agrc != 1)
	{
		//delete persistently
		int result = delete_etherstub( name, 1, NULL);
		printf( "Remove etherstub functon result: %d \n", result );
	}
	else
	{
		//create persistently
		int result = create_etherstub( name, 1, NULL );
		printf( "Created etherstub %c %s functon result: %d \n", name[0], name, result );
	}

	return 0;
}

