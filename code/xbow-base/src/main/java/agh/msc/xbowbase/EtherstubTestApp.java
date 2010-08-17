package agh.msc.xbowbase;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.jna.JNAEtherstubHelper;
import agh.msc.xbowbase.lib.EtherstubHelper;

/**
 *
 * @author robert boczek
 */
public class EtherstubTestApp {

    public static void main(String []args) throws Exception{

        EtherstubHelper etherstubadm = new JNAEtherstubHelper();

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
        parameter = etherstubadm.getEtherstubParameter("etherstub1", LinkParameters.MTU);
        System.out.println("Read MTU property value : " + parameter);

        //reading statistic property
        String stat = null;
        stat = etherstubadm.getEtherstubStatistic("etherstub1", LinkStatistics.IPACKETS);
        System.out.println("Read IPACKETS statistic value is: " + stat);

        //reading properties property
        String property = null;
        property = etherstubadm.getEtherstubProperty("etherstub1", LinkProperties.PRIORITY);
        System.out.println("Read PRIORITY property value is: " + property);

        //setting properties property
        etherstubadm.setEtherstubProperty("etherstub1", LinkProperties.PRIORITY, "HIGH");
    }
}
