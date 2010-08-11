#include "memory.h"

void free_char_array( char **array ){

	if(array != NULL){
		int i = 0;		
		while(array[i] != NULL){
			free(array[i++]);
		}
		free(array);
	}
}

void free_char_string( char *string ){

	free(string);

}

