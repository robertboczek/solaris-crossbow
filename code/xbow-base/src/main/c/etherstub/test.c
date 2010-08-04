#include <stdio.h>

#include "etherstub_wrapper.h"


int main( int agrc, char** argv )
{
	init();

	char name[] = "etherstub1";

	if(agrc == 1)
	{
		//delete persistently
		int result = delete_etherstub( name, 1, NULL);
		printf( "Remove etherstub functon result: %d \n", result );
	}
	else if(agrc == 2)
	{
		//create persistently
		int result = create_etherstub( name, 1, NULL );
		printf( "Created etherstub %c %s functon result: %d \n", name[0], name, result );
	}else{
		char **names;
		int number_of_el;
		if(get_etherstub_names(&names, &number_of_el) == 0){
			printf("%d \n", number_of_el);
			for(int i=0; i<number_of_el; i++){
				printf("%s \n", names[i]);
			}
		}
	}

	return 0;
}

