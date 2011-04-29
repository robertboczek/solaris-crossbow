#include <libdladm.h>

#include <stdlib.h>

#include <common/defs.h>

#include <vlan/memory.h>


vlan_infos_t* malloc_vlan_infos( size_t len )
{
	vlan_infos_t* vlan_infos = malloc( sizeof( *vlan_infos ) );

	vlan_infos->vlan_infos = malloc( ( len + 1 ) * sizeof( *( vlan_infos->vlan_infos ) ) );

	for ( int i = 0; i < len; ++i )
	{
		vlan_infos->vlan_infos[ i ] = malloc_vlan_info();
	}
	vlan_infos->vlan_infos[ len ] = NULL;

	return vlan_infos;
}


void free_vlan_infos( vlan_infos_t* vlan_infos )
{
	for ( vlan_info_t** vlan_info_it = vlan_infos->vlan_infos;
	      *vlan_info_it != NULL;
	      ++vlan_info_it )
	{
		free_vlan_info( *vlan_info_it );
	}

	free( vlan_infos->vlan_infos );
	free( vlan_infos );
}


vlan_info_t* malloc_vlan_info( void )
{
	vlan_info_t* vlan_info = malloc( sizeof( *vlan_info ) );

	vlan_info->name = malloc( MAXLINKNAMELEN );
	vlan_info->link = malloc( MAXLINKNAMELEN );

	return vlan_info;
}


void free_vlan_info( vlan_info_t* vlan_info )
{
	free( vlan_info->name );
	free( vlan_info->link );

	free( vlan_info );
}

