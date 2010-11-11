#ifndef FLOWADM_WRAPPER_AUX_H
#define FLOWADM_WRAPPER_AUX_H

#include "types.h"


/**
 * Returns the pointer to NULL character that terminates string s.
 *
 * \param  s  the string
 *
 * \return  pointer to terminating NULL character
 */
char* strend( char* s );


/**
 * \brief  ,,Flattens'' key-value-pairs structure.
 *
 * Transforms the structure to string representation:
 * key0=value0,key1=value1,...,keyN=valueN
 *
 * \param  kvps  key-value-pairs structure to transform
 *
 * \return  string representation of kvps
 *
 * \warning  The caller is responsible for freeing returned pointer
 *           with free function.
 */
char* flatten_key_value_pairs( key_value_pairs_t* kvps );

#endif

