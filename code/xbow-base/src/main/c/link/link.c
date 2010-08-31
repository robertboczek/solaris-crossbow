#include <kstat.h>
#include <libdllink.h>
#include <libdllink.h>
#include <libdlvnic.h>
#include <libdlstat.h>

#include <stdlib.h>
#include <string.h>

#include "../common/mappings.h"

#include "functor.h"
#include "link.h"
#include "memory.h"
#include "types.h"
#include "defs.h"

#include <netinet/vrrp.h>

#define MAXVNIC		256
#define MAXLENGTH	100

dladm_handle_t handle = 0;


int init()
{
	init_mapping();

	return map_status( dladm_open( &handle ) );
}


int collect_nic_info( dladm_handle_t handle,
                      datalink_id_t link_id, void* arg )
{
	char name[ MAXLINKNAMELEN ];
	nic_info_t*** nic_infos_it = arg;

	dladm_datalink_id2info( handle, link_id, NULL, NULL, NULL,
	                        name, sizeof( name ) );

	free_nic_info( **nic_infos_it );
	**nic_infos_it = get_nic_info( name );

	++( *nic_infos_it );

	return DLADM_WALK_CONTINUE;
}


nic_info_t* get_nic_info( char* name )
{
	nic_info_t* nic_info = malloc_nic_info();

	strcpy( nic_info->name, name );

	// NIC state
	
	// Assume the NIC is down (or unknown).
	
	nic_info->up = 0;

	kstat_ctl_t* kcp = kstat_open();
	if ( kcp != NULL )
	{
		kstat_t* ksp = kstat_lookup( kcp, "link", 0, name );
		if ( ksp != NULL )
		{
			if ( kstat_read( kcp, ksp, NULL ) != -1 )
			{
				link_state_t link_state;

				// TODO-DAWID: rewrite dladm_kstat_value?
				if ( dladm_kstat_value( ksp, "link_state", KSTAT_DATA_UINT32, &link_state ) >= 0 )
				{
					nic_info->up = ( LINK_STATE_UP == link_state );
				}
				else
				{
					// TODO-DAWID: error
				}
			}
			else
			{
				// TODO-DAWID: error
			}
		}
		else
		{
			// TODO-DAWID: error
		}

		kstat_close( kcp );
	}
	else
	{
		// TODO-DAWID: error here
	}

	return nic_info;
}


nic_infos_t* get_nic_infos( void )
{
	int nic_count = 0;

	// Count NICs.

	dladm_walk_datalink_id( &count, handle, &nic_count,
	                        DATALINK_CLASS_PHYS, DATALINK_ANY_MEDIATYPE,
	                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );

	// Allocate and fill nic_infos structure.

	nic_infos_t* nic_infos = malloc_nic_infos( nic_count );

	nic_info_t** nic_infos_it = nic_infos->nic_infos;

	dladm_walk_datalink_id( &collect_nic_info, handle, &nic_infos_it,
	                        DATALINK_CLASS_PHYS, DATALINK_ANY_MEDIATYPE,
	                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );

	nic_infos->nic_infos_len = nic_infos_it - nic_infos->nic_infos;

	return nic_infos;
}

int delete_vnic( char* name, int temporary )
{

	datalink_id_t vnic_linkid;
	dladm_status_t status;

	//indicates whether should be removed temporary or persistently
	uint32_t flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;
	if( temporary != 0 ){
		flags &= ~DLADM_OPT_PERSIST;
	}


	status = dladm_name2info(handle, name, &vnic_linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_INVALID_NAME; 

	status = dladm_vnic_delete(handle, vnic_linkid, flags);

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_OPERATION_FAILURE;

	return XBOW_STATUS_OK;
}


int create_vnic( char* name, int temporary, char *parent )
{
	uint32_t flags;
	dladm_status_t status;
	char vnic_name[MAXLINKNAMELEN];
	char parent_link_name[MAXLINKNAMELEN];
	uchar_t	*mac_addr = NULL;
	uint_t	maclen = 0;
	datalink_id_t vnic_linkid, parent_linkid;

	vnic_name[0] = '\0';
	parent_link_name[0] = '\0';

	flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;

	if( temporary != 0 ){
		flags &= ~DLADM_OPT_PERSIST;
	}

	if (strlcpy(parent_link_name, parent, MAXLINKNAMELEN) >= MAXLINKNAMELEN)
		return XBOW_STATUS_TOO_LONG_PARENT_NAME;

	if (dladm_name2info(handle, parent, &parent_linkid, NULL, NULL, NULL) !=
	    DLADM_STATUS_OK)
		return XBOW_STATUS_INVALID_PARENT_NAME;

	if (strlcpy(vnic_name, name, MAXLINKNAMELEN) >= MAXLINKNAMELEN)
		return XBOW_STATUS_TOO_LONG_NAME;

	if (!dladm_valid_linkname(vnic_name))
		return XBOW_STATUS_INVALID_NAME;
	

	status = dladm_vnic_create(handle, vnic_name, parent_linkid,
	    VNIC_MAC_ADDR_TYPE_AUTO, mac_addr, maclen, NULL, 0, 0,
	    VRRP_VRID_NONE, AF_UNSPEC, &vnic_linkid, NULL, flags);

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_OPERATION_FAILURE;

	return XBOW_STATUS_OK;
}


int get_name(const char *name, void *prop){
	
	link_names_t* link_names =  (link_names_t*)prop;
	link_names->array[link_names->number_of_elements] = (char*)malloc(sizeof(char)*(strlen(name)+1));
	strcpy(link_names->array[link_names->number_of_elements++], name);

}

/** Gets all names of links in the system */
char** get_link_names( int link_type )
{

	uint32_t		flags = DLADM_OPT_ACTIVE;

	link_names_t link_names;
	datalink_class_t data_link_class;

	link_names.array = (char**)malloc(sizeof(char*) * MAXVNIC);
	link_names.number_of_elements = 0;

	if(link_type == 0){
		data_link_class = DATALINK_CLASS_VNIC;
	}else{
		data_link_class = DATALINK_CLASS_PHYS;
	}
	
	//walks through all etherstub's and invokes get_name function
	if( dladm_walk(get_name, handle, &link_names,
	    data_link_class, DATALINK_ANY_MEDIATYPE, flags) != DLADM_STATUS_OK){
		int i;
		for(i = 0; i < link_names.number_of_elements; i++){
			free(link_names.array[link_names.number_of_elements]);
		}
		free(link_names.array);
		return NULL;
	}

	//the element after the last one must be null
	link_names.array[link_names.number_of_elements] = NULL;

	return link_names.array;
}

char* get_link_parameter( char *name, char* parameter)
{

	dladm_status_t status;
	datalink_id_t linkid;
	uint_t		maxpropertycnt = 1;

	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return NULL;

	char *value = (char*)malloc(MAXLENGTH);

	status = dladm_get_linkprop(handle, linkid,
			    DLADM_PROP_VAL_CURRENT, parameter, &value, &maxpropertycnt);

	if (status != DLADM_STATUS_OK){
		free(value);
		value = NULL;
	}
	return value;
}

char* get_link_statistic( char *name, char* property){

	dladm_status_t status;
	datalink_id_t linkid;
	kstat_ctl_t	*kcp;
	kstat_t		*ksp;
	pktsum_t 	stats;

	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return NULL;

	if ((kcp = kstat_open()) == NULL) {
		warn("kstat_open operation failed");
		return NULL;
	}

	ksp = dladm_kstat_lookup(kcp, "link", 0, name, NULL);

	if (ksp != NULL)
		dladm_get_stats(kcp, ksp, &stats);

	(void) kstat_close(kcp);
	char* tmp = (char*)malloc(sizeof(char)*MAXLENGTH);

	if(strcmp(property, "IPACKETS") == 0){
		sprintf(tmp, "%d", (int)stats.ipackets); 
	}else if(strcmp(property, "IERRORS") == 0){
		sprintf(tmp, "%d", (int)stats.ierrors); 
	}else if(strcmp(property, "OPACKETS") == 0){
		sprintf(tmp, "%d", (int)stats.opackets); 
	}else if(strcmp(property, "OERRORS") == 0){
		sprintf(tmp, "%d", (int)stats.oerrors); 
	}else if(strcmp(property, "RBYTES") == 0){
		sprintf(tmp, "%d", (int)stats.rbytes); 
	}else if(strcmp(property, "OBYTES") == 0){
		sprintf(tmp, "%d", (int)stats.obytes); 
	}

	return tmp;
}


int set_link_property( char *name, char* property, char *value)
{

	dladm_status_t status;
	datalink_id_t linkid;
	uint32_t	flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST | DLADM_OPT_FORCE;
	uint_t		maxpropertycnt = 1;	

	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_INVALID_NAME;

	
	status = dladm_set_linkprop(handle, linkid,
			    property, &value, maxpropertycnt, flags);

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_OPERATION_FAILURE;

	return XBOW_STATUS_OK;
}

char* get_link_property( char *name, char* property )
{
	dladm_status_t status;
	datalink_id_t linkid;
	uint_t		maxpropertycnt = 1;

	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return NULL;

	char *value = (char*)malloc(sizeof(char)*MAXLENGTH);

	status = dladm_get_linkprop(handle, linkid,
			    DLADM_PROP_VAL_CURRENT, property, &value, &maxpropertycnt);

	if (status != DLADM_STATUS_OK){
		free(value);
		return NULL;
	}

	return value;
}

void in_getaddr(char *s, struct sockaddr *saddr, int *plenp)
{
	struct sockaddr_in *sin = (struct sockaddr_in *)saddr;
	struct hostent *hp;
	struct netent *np;
	char str[BUFSIZ];
	int error_num;

	(void) strncpy(str, s, sizeof (str));

	(void) memset(sin, 0, sizeof (*sin));


	hp = getipnodebyname(str, AF_INET, 0, &error_num);
	if (hp) {
		sin->sin_family = hp->h_addrtype;
		(void) memcpy(&sin->sin_addr, hp->h_addr, hp->h_length);
		freehostent(hp);
		return;
	}
	np = getnetbyname(str);
	if (np) {
		sin->sin_family = np->n_addrtype;
		sin->sin_addr = inet_makeaddr(np->n_net, INADDR_ANY);
		return;
	}
	
	exit(1);
}

int set_ip_address(char *link, char *address)
{

	int s;
	struct lifreq lifr;
	struct	sockaddr_storage laddr;
	int prefixlen = 0;

	(void) strncpy(lifr.lifr_name, link, strlen(link)+1);

	in_getaddr(address, (struct sockaddr *)&laddr, &prefixlen);

	s = socket(AF_INET, SOCK_DGRAM, 0);

	if (s == -1){
		//couldn't create a socket
		return XBOW_STATUS_OPERATION_FAILURE;
	}

	lifr.lifr_addr = laddr;
	lifr.lifr_addr.ss_family = AF_INET;
	if (ioctl(s, SIOCSLIFADDR, (caddr_t)&lifr) < 0) {
		//couldn't set the ip address
		return XBOW_STATUS_OPERATION_FAILURE;		
	}

	return XBOW_STATUS_OK;
}

char* get_ip_address(char *link)
{

	int s;
	struct lifreq lifr;
	struct sockaddr_in	*sin;

	(void) strncpy(lifr.lifr_name, link, strlen(link)+1);

	s = socket(AF_INET, SOCK_DGRAM, 0);

	if (s == -1){
		//couldn't create socket
		return NULL;
	}

	if (ioctl(s, SIOCGLIFADDR, (caddr_t)&lifr) < 0) {
		//couldn't read ip address value
		return NULL;
		
	}	

	sin = (struct sockaddr_in *)&lifr.lifr_addr;

	if(sin != NULL){
		return inet_ntoa(sin->sin_addr);
	}else{
		return NULL;
	}
}
