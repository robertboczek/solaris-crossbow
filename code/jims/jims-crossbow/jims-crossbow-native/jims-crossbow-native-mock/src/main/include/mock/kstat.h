#ifndef MOCK_KSTAT_H
#define MOCK_KSTAT_H

#include <libdladm.h>
#include <libdlstat.h>


kstat_ctl_t* kstat_open( void );

kstat_t* dladm_kstat_lookup( kstat_ctl_t* ctl, const char* link, int a,
                             const char* vnic, const char* b );

void dladm_get_stats( kstat_ctl_t* ctl, kstat_t* t, pktsum_t* stats );

int kstat_close( kstat_ctl_t* ctl );

#endif

