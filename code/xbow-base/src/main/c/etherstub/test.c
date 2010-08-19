#include <stdio.h>

#include "etherstub.h"


int main( int agrc, char** argv )
{
	init();

	if(strcmp(argv[1], "delete") == RESULT_OK)
	{
		//delete persistently
		int result = delete_etherstub( argv[2], 0 );
		printf( "Delete etherstub functon result: %d \n", result );
	}
	else if(strcmp(argv[1], "create") == RESULT_OK)
	{
		//create persistently
		int result = create_etherstub( argv[2], 0 );
		printf( "Created etherstub %s functon result: %d \n", argv[1], result );
	}
	else if(strcmp(argv[1], "list") == RESULT_OK)
	{
		
		char **names;
		printf(" List of all etherstubs: \n");
		names = get_etherstub_names();
		if(names != NULL){
			int i = 0;
			while(names[i] != NULL){
				printf("%s\n", names[i++]);
			}	
		}
	}
	else if(strcmp(argv[1], "get") == RESULT_OK)
	{

		char *value = get_etherstub_parameter(argv[2], argv[3]);
		printf("%s \n", value);

		free(value);
	}
	else if(strcmp(argv[1], "statistic") == RESULT_OK)
	{

		char *value = get_etherstub_statistic(argv[2], argv[3]);
		printf("%s \n", value);
		free(value);
	}
	else if(strcmp(argv[1], "setproperty") == RESULT_OK)
	{

		char *value = (char*)malloc(sizeof(char)*20);
		strcpy(value, argv[4]);

		if(set_etherstub_property(argv[2], argv[3], value) == RESULT_OK)
		{
			printf("%s \n", value);
		}
		
		free(value);
	}
	else if(strcmp(argv[1], "getproperty") == RESULT_OK)
	{

		char *value = get_etherstub_property(argv[2], argv[3]);
		printf("Property value is: %s \n", value);
		free(value);
	}
	return 0;
}

