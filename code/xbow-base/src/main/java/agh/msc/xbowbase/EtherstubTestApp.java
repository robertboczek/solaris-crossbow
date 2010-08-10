package agh.msc.xbowbase;

import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
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

        //reading parameter property
        String parameter = null;
        parameter = etherstubadm.getEtherstubParameter("etherstub1", EtherstubParameters.MTU);
        System.out.println("Read MTU property value : " + parameter);

        //reading statistic property
        String stat = null;
        stat = etherstubadm.getEtherstubStatistic("etherstub1", EtherstubStatistics.IPACKETS);
        System.out.println("Read IPACKETS statistic value is: " + stat);

        //reading properties property
        String property = null;
        property = etherstubadm.getEtherstubProperty("etherstub1", EtherstubProperties.PRIORITY);
        System.out.println("Read PRIORITY property value is: " + property);

        //setting properties property
        etherstubadm.setEtherstubProperty("etherstub1", EtherstubProperties.PRIORITY, "HIGH");
    }
}
