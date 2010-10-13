#include <string.h>

#include <mock/cmockery.h>
#include <mock/kstat.h>


kstat_ctl_t* kstat_open( void )
{
	return ( kstat_ctl_t* ) mock();
}


kstat_t* dladm_kstat_lookup( kstat_ctl_t* ctl, const char* link, int a,
                             const char* vnic, const char* b )
{
	check_expected( vnic );

	return ( kstat_t* ) mock();
}


void dladm_get_stats( kstat_ctl_t* ctl, kstat_t* t, pktsum_t* stats )
{
	memcpy( stats, mock(), sizeof( *stats ) );
}


int kstat_close( kstat_ctl_t* ctl )
{
	return ( int ) mock();
}

