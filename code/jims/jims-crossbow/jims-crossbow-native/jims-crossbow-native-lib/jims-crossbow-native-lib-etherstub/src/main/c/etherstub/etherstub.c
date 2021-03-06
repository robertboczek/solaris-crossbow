#include <kstat.h>

#include <libdladm.h>
#include <libdllink.h>
#include <libdlvnic.h>
#include <libdlstat.h>

#include <string.h>
#include <stropts.h>

#include <netinet/vrrp.h>

#include <etherstub/etherstub.h>

#define MAXVNIC		256
#define MAXLENGTH	100

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


int delete_etherstub( char* name, int temporary )
{

	datalink_id_t linkid;
	dladm_status_t status;

	//indicates whether should be removed temporary or persistently
	uint32_t flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;
	if( temporary != 0 ){
		flags &= ~DLADM_OPT_PERSIST;
	}


	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_INVALID_NAME; 

	status = dladm_vnic_delete(handle, linkid, flags);

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_OPERATION_FAILURE;

	return XBOW_STATUS_OK;
}

int create_etherstub( char* name, int temporary )
{
	uint32_t flags;
	dladm_status_t status;
	char etherstub_name[MAXLINKNAMELEN] = { 0 };
	uchar_t mac_addr[ETHERADDRL] = { 0 };

	flags = DLADM_OPT_ANCHOR | DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;

	if( temporary != 0 ){
		flags &= ~DLADM_OPT_PERSIST;
	}

	if (strlcpy(etherstub_name, name, MAXLINKNAMELEN) >= MAXLINKNAMELEN)
		return  XBOW_STATUS_TOO_LONG_NAME;

	if (!dladm_valid_linkname(etherstub_name))
		return XBOW_STATUS_INVALID_NAME;
	

	status = dladm_vnic_create(handle, etherstub_name, DATALINK_INVALID_LINKID,
	    VNIC_MAC_ADDR_TYPE_AUTO, mac_addr, ETHERADDRL, NULL, 0, 0,
	    VRRP_VRID_NONE, AF_UNSPEC, NULL, NULL, flags);

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_OPERATION_FAILURE;
	return XBOW_STATUS_OK;
}

int get_name(const char *name, void *prop){
	
	etherstub_names_t* etherstub_names =  (etherstub_names_t*)prop;
	etherstub_names->array[etherstub_names->number_of_elements] = (char*)malloc(sizeof(char)*(strlen(name)+1));
	strcpy(etherstub_names->array[etherstub_names->number_of_elements++], name);
}

/** Gets all names of etherstubs in the system */
char** get_etherstub_names()
{

	uint32_t		flags = DLADM_OPT_ACTIVE;

	etherstub_names_t etherstub_names;

	etherstub_names.array = (char**)malloc(sizeof(char*) * MAXVNIC);
	etherstub_names.number_of_elements = 0;

	//walks through all etherstub's and invokes get_name function
	if( dladm_walk(get_name, handle, &etherstub_names,
	    DATALINK_CLASS_ETHERSTUB, DATALINK_ANY_MEDIATYPE, flags) != DLADM_STATUS_OK){

		free(etherstub_names.array);
		return NULL;
	}

	//the element after the last one must be null
	etherstub_names.array[etherstub_names.number_of_elements] = NULL;

	return etherstub_names.array;
}

char* get_etherstub_parameter( char *name, char* parameter)
{

	dladm_status_t status;
	datalink_class_t	class;
	datalink_id_t linkid;
	uint_t		maxpropertycnt = 1;
	uint32_t	flags = DLADM_OPT_ACTIVE;

	status = dladm_name2info(handle, name, &linkid, NULL, &class,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return NULL;

	char *value = (char*)malloc(MAXLENGTH);

	dladm_vnic_attr_t	vinfo;

	if(strcasecmp("OVER", parameter) == 0)
	{
		if (dladm_vnic_info(handle, linkid, &vinfo, flags) !=
		    DLADM_STATUS_OK) {
			(void) strcpy(value, "?");
			return value;
		}
		
		if (dladm_datalink_id2info(handle, vinfo.va_link_id, NULL, NULL,
		    NULL, value, MAXLENGTH) != DLADM_STATUS_OK)
			(void) strcpy(value, "?");
	}
	else if(strcasecmp("MTU", parameter) == 0 || strcasecmp("BRIDGE", parameter) == 0 || strcasecmp("STATE", parameter) == 0)
	{
		status = dladm_get_linkprop(handle, linkid,
			    DLADM_PROP_VAL_CURRENT, parameter, &value, &maxpropertycnt);
	}
	else if(strcasecmp("CLASS", parameter) == 0)
	{
		(void) dladm_class2str(class, value);
	}


	if (status != DLADM_STATUS_OK)
	{
		free(value);
		value = NULL;
	}
	return value;
}

char* get_etherstub_statistic( char *name, char* property){

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

	if(strcasecmp(property, "IPACKETS") == 0){
		sprintf(tmp, "%d", (int)stats.ipackets); 
	}else if(strcasecmp(property, "IERRORS") == 0){
		sprintf(tmp, "%d", (int)stats.ierrors); 
	}else if(strcasecmp(property, "OPACKETS") == 0){
		sprintf(tmp, "%d", (int)stats.opackets); 
	}else if(strcasecmp(property, "OERRORS") == 0){
		sprintf(tmp, "%d", (int)stats.oerrors); 
	}else if(strcasecmp(property, "RBYTES") == 0){
		sprintf(tmp, "%d", (int)stats.rbytes); 
	}else if(strcasecmp(property, "OBYTES") == 0){
		sprintf(tmp, "%d", (int)stats.obytes); 
	}else{
		free(tmp);
		tmp = NULL;
	}

	return tmp;
}

int set_etherstub_property( char *name, char* property, char *value)
{

	dladm_status_t status;
	datalink_id_t linkid;
	uint32_t	flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST | DLADM_OPT_FORCE;
	uint_t		maxpropertycnt = 1;	

	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_INVALID_NAME;

	
	if(value != NULL)
	{
		status = dladm_set_linkprop(handle, linkid,
			    property, &value, maxpropertycnt, flags);
	} else {
		status = dladm_set_linkprop(handle, linkid,
			    property, NULL, 0, flags);
	}

	if (status != DLADM_STATUS_OK)
		return XBOW_STATUS_OPERATION_FAILURE;

	return XBOW_STATUS_OK;
}

char* get_etherstub_property( char *name, char* property )
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

int reset_prop( char *name, char* property)
{

	return set_etherstub_property(name, property, NULL);
}

