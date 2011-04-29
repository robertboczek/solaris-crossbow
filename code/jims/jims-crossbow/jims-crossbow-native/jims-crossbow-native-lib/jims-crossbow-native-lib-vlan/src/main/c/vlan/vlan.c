#include <libdladm.h>
#include <libdllink.h>
#include <libdlvlan.h>

#include <common/defs.h>
#include <common/mappings.h>

#include <vlan/types.h>


dladm_handle_t handle = 0;

int init()
{
	init_mapping();

	return map_status( dladm_open( &handle ) );
}


int vlan_create( vlan_info_t* info )
{
	dladm_status_t rc = { 0 };

	dladm_arg_list_t* proplist = NULL;
	uint32_t flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;
	datalink_id_t parentid, linkid;

	if ( DLADM_STATUS_OK != dladm_name2info( handle, info->link, &parentid,
	                                         NULL, NULL, NULL ) )
	{
	
	}
	else
	{
		rc = dladm_vlan_create( handle, info->name, parentid, ( uint16_t ) info->tag,
		                        proplist, flags, &linkid );
	}

	return map_status( rc );
}


int vlan_remove( char* name )
{
	datalink_id_t vlanid = { 0 };
	dladm_status_t rc = { 0 };

	if ( DLADM_STATUS_OK != dladm_name2info( handle, name, &vlanid,
	                                         NULL, NULL, NULL ) )
	{
	
	}
	else
	{
		rc = dladm_vlan_delete( handle, vlanid, DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );
	}

	return map_status( rc );
}


// TODO  move the functor to common?
int count_links( dladm_handle_t handle, datalink_id_t link_id, void* counter )
{
	*( ( int* ) counter ) += 1;
	return DLADM_WALK_CONTINUE;
}


int collect_link_names( dladm_handle_t handle,
                        datalink_id_t link_id, void* arg )
{
	char** it = arg;

	dladm_datalink_id2info( handle, link_id, NULL, NULL, NULL,
	                        *it, MAXLINKNAMELEN );

	*it += MAXLINKNAMELEN;

	return DLADM_WALK_CONTINUE;
}


vlan_infos_t* get_vlan_infos()
{
	char* links;
	int links_len = 0;

	dladm_walk_datalink_id( &count_links, handle, &links_len,
	                        DATALINK_CLASS_VLAN, DATALINK_ANY_MEDIATYPE,
	                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );

	char* links_it = links = malloc( links_len * MAXLINKNAMELEN );

	dladm_walk_datalink_id( &collect_link_names, handle, &links_it,
	                        DATALINK_CLASS_VLAN, DATALINK_ANY_MEDIATYPE,
	                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );

	vlan_infos_t* infos = malloc_vlan_infos( links_len );
	infos->len = links_len;

	for ( int i = 0; i < links_len; ++i )
	{
		char* name = links + i * MAXLINKNAMELEN;
		datalink_id_t vlanid = { 0 };
		dladm_vlan_attr_t	vinfo;
		char parent[ MAXLINKNAMELEN ] = { 0 };

		strcpy( infos->vlan_infos[ i ]->name, name );

		if ( DLADM_STATUS_OK != dladm_name2info( handle, name, &vlanid,
		                                         NULL, NULL, NULL ) )
		{
		}
		else
		{
			dladm_vlan_info( handle, vlanid, &vinfo, DLADM_OPT_ACTIVE );

			if ( DLADM_STATUS_OK != dladm_datalink_id2info( handle, vinfo.dv_linkid,
			                                                NULL, NULL, NULL,
			                                                parent, sizeof( parent ) ) )
			{
			}
			else
			{
				infos->vlan_infos[ i ]->tag = vinfo.dv_vid;
				strcpy( infos->vlan_infos[ i ]->link, parent );
			}
		}
	}

	free( links );

	return infos;
}

