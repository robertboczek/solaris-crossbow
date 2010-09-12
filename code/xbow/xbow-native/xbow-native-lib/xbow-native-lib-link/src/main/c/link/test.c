#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include <common/defs.h>

#include <link/ip.h>
#include <link/link.h>


int main( int agrc, char** argv )
{
	init();

	if(strcmp(argv[1], "delete") == XBOW_STATUS_OK)
	{
		//delete persistently
		int result = delete_vnic( argv[2], 0 );
		printf( "Delete link functon result: %d \n", result );
	}
	else if(strcmp(argv[1], "create") == XBOW_STATUS_OK)
	{
		//create persistently
		int result = create_vnic( argv[2], 0, argv[3] );
		printf( "Created link %s under link %s functon result: %d \n", argv[2], argv[3], result );
	}
	else if(strcmp(argv[1], "setip") == XBOW_STATUS_OK)
	{
		//set ip
		int result = set_ip_address( argv[2], argv[3] );
		printf( "changed ip of link %s for: %s functon result: %d \n", argv[2], argv[3], result );
	}
	else if(strcmp(argv[1], "getip") == XBOW_STATUS_OK)
	{
		//get ip
		char *ip = get_ip_address( argv[2] );
		printf( "checked ip of link %s functon result: %s \n", argv[2], ip );
	}
	else if(strcmp(argv[1], "list") == XBOW_STATUS_OK)
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
	else if(strcmp(argv[1], "get") == XBOW_STATUS_OK)
	{

		char *value = get_link_parameter(argv[2], argv[3]);
		printf("%s \n", value);

		free(value);
	}
	else if(strcmp(argv[1], "statistic") == XBOW_STATUS_OK)
	{

		char *value = get_link_statistic(argv[2], argv[3]);
		printf("%s \n", value);
		free(value);
	}
	else if(strcmp(argv[1], "setproperty") == XBOW_STATUS_OK)
	{

		char *value = (char*)malloc(sizeof(char)*20);
		strcpy(value, argv[4]);

		if(set_link_property(argv[2], argv[3], value) == XBOW_STATUS_OK)
		{
			printf("%s \n", value);
		}
		
		free(value);
	}
	else if(strcmp(argv[1], "getproperty") == XBOW_STATUS_OK)
	{

		char *value = get_link_property(argv[2], argv[3]);
		printf("Property value is: %s \n", value);
		free(value);
	}
	return 0;
}

