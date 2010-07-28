package agh.msc.xbowbase.etherstub;

import java.util.Map;

/**
 *
 * @author robert
 */
public class Etherstub implements EtherstubMBean{

    private String name;
    private boolean temporary;
    private String rootDir;
    private Map<String, String> parameters;
    private Map<String, String> properties;

    public Etherstub(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTemporary() {
        return this.temporary;
    }

    @Override
    public String getRootDir() {
        return this.rootDir;
    }

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @Override
    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public void setTemporary(boolean temporary){
        this.temporary = temporary;
    }

    public void setRootDir(String rootDir){
        this.rootDir = rootDir;
    }

    public void setProperties(Map<String, String> properties){
        this.properties = properties;
    }

    public void setParameters(Map<String, String> parameters){
        this.parameters  = parameters;
    }
    
}
