struct dladm_handle;
typedef struct dladm_handle* dladm_handle_t;

dladm_handle_t handle = 0;


int init()
{
	return dladm_open( &handle );
}


int remove( char* flow )
{
	return dladm_flow_remove( handle, flow, 0 );
}

