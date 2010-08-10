package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.lib.Etherstubadm;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author robert boczek
 */
public class EtherstubManager implements EtherstubManagerMBean {

    private Etherstubadm etherstubadm;
    private Set<EtherstubMBean> etherstubsSet;

    public EtherstubManager() {
        this.etherstubsSet = new HashSet<EtherstubMBean>();
    }

    @Override
    public void create(EtherstubMBean etherstubMBean) throws EtherstubException {
        this.etherstubadm.createEtherstub(etherstubMBean.getName(), etherstubMBean.isTemporary());
    }

    @Override
    public void delete(String name, boolean temporary) throws EtherstubException {
        this.etherstubadm.deleteEtherstub(name, temporary);
    }

    @Override
    public List<String> getEtherstubsNames() throws EtherstubException {
        return Arrays.asList(this.etherstubadm.getEtherstubNames());
    }

    @Override
    public void discover() throws EtherstubException {

        Set<EtherstubMBean> currentMBeans = convertToSet(this.etherstubadm.getEtherstubNames());

        //check for new Etherstubs
        for (EtherstubMBean etherstubMBean : currentMBeans) {
            if (this.etherstubsSet.contains(etherstubMBean) == false) {
                //@todo create and register new EtherstubMBean
            }
        }

        //remove etherstubs that don't exist anymore
        for (EtherstubMBean etherstubMBean : this.etherstubsSet) {
            if (currentMBeans.contains(etherstubMBean) == false) {
                //@todo remove and unregister EtherstubMBean
            }
        }
    }

    public void setEtherstubadm(Etherstubadm etherstubadm) {
        this.etherstubadm = etherstubadm;
    }

    private Set<EtherstubMBean> convertToSet(String[] etherstubNames) {
        Set<EtherstubMBean> set = new HashSet<EtherstubMBean>();
        for (String etherstubMBeanName : etherstubNames) {
            set.add(new Etherstub(etherstubMBeanName, false));
        }
        return set;
    }
}
