
#include "etherstub_wrapper.h"
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

etherstub_return_type_t delete_etherstub( char* name, int temporary )
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
		return INVALID_ETHERSTUB_NAME; 

	status = dladm_vnic_delete(handle, linkid, flags);

	if (status != DLADM_STATUS_OK)
		return DELETE_FAILURE;

	return RESULT_OK;
}

etherstub_return_type_t create_etherstub( char* name, int temporary )
{
	uint32_t flags;
	dladm_status_t status;
	char etherstub_name[MAXLINKNAMELEN];
	uchar_t mac_addr[ETHERADDRL];

	etherstub_name[0] = '\0';
	bzero(mac_addr, sizeof (mac_addr));

	flags = DLADM_OPT_ANCHOR | DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;

	if( temporary != 0 ){
		flags &= ~DLADM_OPT_PERSIST;
	}

	if (strlcpy(etherstub_name, name, MAXLINKNAMELEN) >= MAXLINKNAMELEN)
		return TOO_LONG_ETHERSTUB_NAME;

	if (!dladm_valid_linkname(etherstub_name))
		return INVALID_ETHERSTUB_NAME;
	

	status = dladm_vnic_create(handle, etherstub_name, DATALINK_INVALID_LINKID,
	    VNIC_MAC_ADDR_TYPE_AUTO, mac_addr, ETHERADDRL, NULL, 0, 0,
	    VRRP_VRID_NONE, AF_UNSPEC, NULL, NULL, flags);

	if (status != DLADM_STATUS_OK)
		return CREATE_FAILURE;
	return RESULT_OK;
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
		return LIST_ETHERSTUB_NAMES_ERROR;
	}

	//the element after the last one must be null
	etherstub_names.array[etherstub_names.number_of_elements] = NULL;

	return etherstub_names.array;
}

char* get_etherstub_parameter( char *name, etherstub_parameter_type_t parameter)
{

	dladm_status_t status;
	datalink_id_t linkid;
	uint_t		maxpropertycnt = 1;

	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return NULL;

	char *parameter_type = (char*)malloc(MAXLENGTH);
	char *value = (char*)malloc(MAXLENGTH);

	switch(parameter){
		case BRIDGE:
			strcpy(parameter_type, "BRIDGE");
			break;
		case OVER:
			strcpy(parameter_type, "OVER");
			break;
		case STATE:
			strcpy(parameter_type, "STATE");
			break;
		case MTU:
			strcpy(parameter_type, "MTU");
			break;
	}

	status = dladm_get_linkprop(handle, linkid,
			    DLADM_PROP_VAL_CURRENT, parameter_type, &value, &maxpropertycnt);

	free(parameter_type);

	if (status != DLADM_STATUS_OK){
		free(value);
		value = NULL;
	}

	return value;
}

char* get_etherstub_statistic( char *name, etherstub_statistic_type_t property){

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

	switch(property){
		case IPACKETS:
			sprintf(tmp, "%d", (int)stats.ipackets); 
			break;
		case IERRORS:
			sprintf(tmp, "%d", (int)stats.ierrors); 
			break;
		case OPACKETS:
			sprintf(tmp, "%d", (int)stats.opackets); 
			break;
		case OERRORS:
			sprintf(tmp, "%d", (int)stats.oerrors); 
			break;
		case RBYTES:
			sprintf(tmp, "%d", (int)stats.rbytes); 
			break;

		case OBYTES:
			sprintf(tmp, "%d", (int)stats.obytes); 
			break;

	}

	return tmp;
}

void set_property_type(etherstub_property_type_t property, char **property_type){

	switch(property){
		case MAXBW:
			strcpy(*property_type, "maxbw");
			break;
		case CPUS:
			strcpy(*property_type, "cpus");
			break;
		case LEARN_LIMIT:
			strcpy(*property_type, "learn_limit");
			break;
		case PRIORITY:
			strcpy(*property_type, "priority");
			break;
	}
}

etherstub_return_type_t set_etherstub_property( char *name, etherstub_property_type_t property, char *value)
{

	dladm_status_t status;
	datalink_id_t linkid;
	uint32_t	flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST | DLADM_OPT_FORCE;
	uint_t		maxpropertycnt = 1;	

	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return INVALID_ETHERSTUB_NAME;

	char *property_type = (char*)malloc(sizeof(char)*MAXLENGTH);

	set_property_type(property, &property_type);
	
	status = dladm_set_linkprop(handle, linkid,
			    property_type, &value, maxpropertycnt, flags);

	free(property_type);

	if (status != DLADM_STATUS_OK)
		return ETHERSTUB_PROPERTY_FAILURE;

	return RESULT_OK;
}

char* get_etherstub_property( char *name, etherstub_property_type_t property )
{
	dladm_status_t status;
	datalink_id_t linkid;
	uint_t		maxpropertycnt = 1;

	status = dladm_name2info(handle, name, &linkid, NULL, NULL,
	    NULL);

	if (status != DLADM_STATUS_OK)
		return NULL;

	char *property_type = (char*)malloc(sizeof(char)*MAXLENGTH);
	char *value = (char*)malloc(sizeof(char)*MAXLENGTH);

	
	set_property_type(property, &property_type);

	status = dladm_get_linkprop(handle, linkid,
			    DLADM_PROP_VAL_CURRENT, property_type, &value, &maxpropertycnt);

	free(property_type);

	if (status != DLADM_STATUS_OK){
		free(value);
		return NULL;
	}

	return value;
}
