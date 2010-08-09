package agh.msc.xbowbase;

import agh.msc.xbowbase.jna.JNAEtherstubadm;
import agh.msc.xbowbase.lib.Etherstubadm;

/**
 *
 * @author robert boczek
 */
public class EtherstubTestApp {

    public static void main(String []args) throws Exception{

        Etherstubadm etherstubadm = new JNAEtherstubadm();

        //test creating etherstub
        etherstubadm.createEtherstub("etherstub23", false);

        //test removing etherstub
        etherstubadm.deleteEtherstub("etherstub23", false);

        String names[] = null;
        names = etherstubadm.getEtherstubNames();
        if(names != null){
            for(String name : names){
                System.out.println("Etherstub name: " + name);
            }
        }
    }
}
