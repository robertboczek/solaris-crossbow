#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "link.h"


int main( int agrc, char** argv )
{
	init();

	if(strcmp(argv[1], "delete") == RESULT_OK)
	{
		//delete persistently
		int result = delete_vnic( argv[2], 0 );
		printf( "Delete link functon result: %d \n", result );
	}
	else if(strcmp(argv[1], "create") == RESULT_OK)
	{
		//create persistently
		int result = create_vnic( argv[2], 0, argv[3] );
		printf( "Created link %s under link %s functon result: %d \n", argv[2], argv[3], result );
	}
	else if(strcmp(argv[1], "list") == RESULT_OK)
	{
		
		char **names;
		int link_type = 0;
		if(strcmp(argv[2], "vnic") == 0){
			printf(" List of all vnic's: \n");
		}else{
			printf(" List of all vnic's: \n");
			link_type = 1;
		}

		names = get_link_names( link_type );

		if(names != NULL){
			int i = 0;
			while(names[i] != NULL){
				printf("%s\n", names[i++]);
			}	
		}
	}
	else if(strcmp(argv[1], "get") == RESULT_OK)
	{

		char *value = get_link_parameter(argv[2], argv[3]);
		printf("%s \n", value);

		free(value);
	}
	else if(strcmp(argv[1], "statistic") == RESULT_OK)
	{

		char *value = get_link_statistic(argv[2], argv[3]);
		printf("%s \n", value);
		free(value);
	}
	else if(strcmp(argv[1], "setproperty") == RESULT_OK)
	{

		char *value = (char*)malloc(sizeof(char)*20);
		strcpy(value, argv[4]);

		if(set_link_property(argv[2], argv[3], value) == RESULT_OK)
		{
			printf("%s \n", value);
		}
		
		free(value);
	}
	else if(strcmp(argv[1], "getproperty") == RESULT_OK)
	{

		char *value = get_link_property(argv[2], argv[3]);
		printf("Property value is: %s \n", value);
		free(value);
	}
	return 0;
}
