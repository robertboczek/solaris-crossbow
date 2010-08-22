package agh.msc.xbowbase.jna.mapping;

import com.sun.jna.Library;
import com.sun.jna.Pointer;


/**
 * C <-> Java mappings.
 *
 * @author robert boczek
 */
public interface EtherstubHandle extends Library {

    public int init();

    public int delete_etherstub(String name, int temporary);

    public int create_etherstub(String name, int temporary);

    public Pointer get_etherstub_names();

    public Pointer get_etherstub_parameter(String name, String parameter);

    public Pointer get_etherstub_statistic(String name, String statistic);

    public Pointer get_etherstub_property(String name, String property);

    public int set_etherstub_property(String name, String property, String value);

    public void free_char_array(Pointer pointer);

    public void free_char_string(Pointer pointer);
}
