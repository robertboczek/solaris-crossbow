#ifndef MOCK_LINK_H
#define MOCK_LINK_H

#include <libdladm.h>
#include <libdllink.h>


dladm_status_t dladm_datalink_id2info( dladm_handle_t handle, datalink_id_t id,
                                       uint32_t* flag, datalink_class_t* link_class,
                                       uint32_t* media, char* link, size_t len );


dladm_status_t dladm_name2info( dladm_handle_t handle, const char* link,
                                datalink_id_t* linkidp, uint32_t* flagp,
                                datalink_class_t* classp, uint32_t* mediap );


dladm_status_t dladm_walk_datalink_id( int ( *fn )( dladm_handle_t, datalink_id_t, void* ),
                                       dladm_handle_t handle, void* argp, datalink_class_t class,
                                       datalink_media_t dmedia, uint32_t flags );


dladm_status_t dladm_walk( dladm_walkcb_t* p, dladm_handle_t handle, void* pointer,
                           datalink_class_t data_link_class, datalink_media_t b, uint32_t flags );

#endif

