
#include "etherstub_wrapper.h"
#define MAXVNIC		256

dladm_handle_t handle = 0;
int number_of_elements = 0;
/* array for etherstub names */
char **names_array = NULL;

typedef struct show_vnic_state {
	datalink_id_t	vs_vnic_id;
	datalink_id_t	vs_link_id;
	char		vs_vnic[MAXLINKNAMELEN];
	char		vs_link[MAXLINKNAMELEN];
	boolean_t	vs_parsable;
	boolean_t	vs_found;
	boolean_t	vs_firstonly;
	boolean_t	vs_donefirst;
	boolean_t	vs_stats;
	boolean_t	vs_printstats;
	pktsum_t	vs_totalstats;
	pktsum_t	vs_prevstats[MAXVNIC];
	boolean_t	vs_etherstub;
	dladm_status_t	vs_status;
	uint32_t	vs_flags;
	ofmt_handle_t	vs_ofmt;
} show_vnic_state_t;


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

int get_name(const char *name, void *prop){
	
	names_array[number_of_elements] = (char*)malloc(sizeof(char)*(strlen(name)+1));
	strcpy(names_array[number_of_elements++], name);
}

int get_etherstub_names( char*** names, int *number_of_etherstubs)
{

	show_vnic_state_t	state;
	dladm_status_t		status;
	datalink_id_t		linkid = DATALINK_ALL_LINKID;
	datalink_id_t		dev_linkid = DATALINK_ALL_LINKID;
	uint32_t		flags = DLADM_OPT_ACTIVE;

	names_array = (char**)malloc(sizeof(char*) * MAXVNIC);
	number_of_elements = 0;

	//walks through all etherstub's and invokes get_name function
	if( dladm_walk(get_name, handle, &state,
	    DATALINK_CLASS_ETHERSTUB, DATALINK_ANY_MEDIATYPE, flags) != DLADM_STATUS_OK){
		return 1;
	}

	(*names) = names_array;
	(*number_of_etherstubs) = number_of_elements;	

	return 0;
}

