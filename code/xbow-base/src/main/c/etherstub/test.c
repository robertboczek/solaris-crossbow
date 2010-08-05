#include <stdio.h>

#include "etherstub_wrapper.h"


int main( int agrc, char** argv )
{
	init();

	if(strcmp(argv[1], "delete") == RESULT_OK)
	{
		//delete persistently
		int result = delete_etherstub( argv[2], PERSISTENT, NULL);
		printf( "Delete etherstub functon result: %d \n", result );
	}
	else if(strcmp(argv[1], "create") == RESULT_OK)
	{
		//create persistently
		int result = create_etherstub( argv[2], PERSISTENT, NULL );
		printf( "Created etherstub %s functon result: %d \n", argv[1], result );
	}
	else if(strcmp(argv[1], "list") == RESULT_OK)
	{
		
		char **names;
		int number_of_el;
		printf(" List of all etherstubs: \n");
		if(get_etherstub_names(&names, &number_of_el) == RESULT_OK){
			printf("%d \n", number_of_el);
			for(int i=0; i<number_of_el; i++){
				printf("%s \n", names[i]);
			}
		}

	}
	else if(strcmp(argv[1], "get") == RESULT_OK)
	{

		etherstub_property_type_t type;
		if(strcmp(argv[3], "BRIDGE") == 0){
			type = BRIDGE;
		}else if(strcmp(argv[3], "MTU") == 0){
			type = MTU;
		}else if(strcmp(argv[3], "STATE") == 0){
			type = STATE;
		}else if(strcmp(argv[3], "OVER") == 0){
			type = OVER;
		}

		char *value = (char*)malloc(sizeof(char)*20);
		if(get_etherstub_property(argv[2], type, &value) == RESULT_OK)
		{
			printf("%s \n", value);
		}
		free(value);
	}
	else if(strcmp(argv[1], "statistic") == RESULT_OK)
	{

		etherstub_statistic_type_t type;
		if(strcmp(argv[3], "IPACKETS") == 0){
			type = IPACKETS;
		}else if(strcmp(argv[3], "RBYTES") == 0){
			type = RBYTES;
		}else if(strcmp(argv[3], "IERRORS") == 0){
			type = IERRORS;
		}else if(strcmp(argv[3], "OPACKETS") == 0){
			type = OPACKETS;
		}else if(strcmp(argv[3], "OBYTES") == 0){
			type = OBYTES;
		}else if(strcmp(argv[3], "OERRORS") == 0){
			type = OERRORS;
		}

		char *value = (char*)malloc(sizeof(char)*20);
		value[0] = '\0';

		if(get_etherstub_statistic(argv[2], type, &value) == RESULT_OK)
		{
			printf("%s \n", value);
		}
		free(value);
	}

	return 0;
}

