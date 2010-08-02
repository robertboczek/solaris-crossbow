
#include "etherstub_wrapper.h"

dladm_handle_t handle = 0;


/**
 * \brief  Initializes the library.
 *
 * \return  0        on success
 * \return  non-zero otherwise
 */
int init()
{
	return dladm_open( &handle );
}

int delete_etherstub( char* name, int temporary, char *rootDir )
{

	datalink_id_t linkid;
	dladm_status_t status;

	//indicates whether should be removed temporary or persistently
	uint32_t flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;
	if( temporary == 0){
		flags &= ~DLADM_OPT_PERSIST;
	}

	/*   @todo change root 
		if (rootDir != NULL)
			altroot_cmd(rootDir, argc, argv);
	*/


	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return 2; 

	status = dladm_vnic_delete(handle, linkid, flags);

	if (status != DLADM_STATUS_OK)
		return 1;

	return 0;
}

int create_etherstub( char* name, int temporary, char *rootDir )
{
	uint32_t flags;
	dladm_status_t status;
	char etherstub_name[MAXLINKNAMELEN];
	uchar_t mac_addr[ETHERADDRL];

	etherstub_name[0] = '\0';
	bzero(mac_addr, sizeof (mac_addr));

	flags = DLADM_OPT_ANCHOR | DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;

	if(temporary == 0){
		flags &= ~DLADM_OPT_PERSIST;
	}

	if (strlcpy(etherstub_name, name, MAXLINKNAMELEN) >= MAXLINKNAMELEN)
		return 2;

	if (!dladm_valid_linkname(etherstub_name))
		return 3;
	
	/* @todo rootdir
	if (rootDir != NULL)
		altroot_cmd(altroot, argc, argv);
	*/

	status = dladm_vnic_create(handle, etherstub_name, DATALINK_INVALID_LINKID,
	    VNIC_MAC_ADDR_TYPE_AUTO, mac_addr, ETHERADDRL, NULL, 0, 0,
	    VRRP_VRID_NONE, AF_UNSPEC, NULL, NULL, flags);

	if (status != DLADM_STATUS_OK)
		return 1;
	return 0;
}
