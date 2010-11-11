#ifndef MOCK_LINK_H
#define MOCK_LINK_H

#include <libdladm.h>


dladm_status_t dladm_open( dladm_handle_t* handle );


// Not a mock, commonly used auxiliary function.

void fill_buffer( void* target, void* source, int len );

#endif

