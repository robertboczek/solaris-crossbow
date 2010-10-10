#ifndef LINK_MOCK_H
#define LINK_MOCK_H

#include <libdladm.h>
#include <libdllink.h>


dladm_status_t dladm_walk_datalink_id( int ( *fn )( dladm_handle_t, datalink_id_t, void* ),
                                       dladm_handle_t handle, void* arg, datalink_class_t class,
                                       datalink_media_t dmedia, uint32_t flags );


dladm_status_t dladm_open( dladm_handle_t* handle );

#endif

