package org.jims.modules.crossbow.gui.validator;

import java.util.HashSet;
import java.util.Set;

import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;

/**
 * New network structure validator
 * 
 * @author robert
 *
 */
public class NetworkValidator {

	/**
	 * Validates input network
	 * 
	 * @param projectId
	 * @param objectModel Object Model
	 * @return Return string with first found error
	 */
	public String validate(String projectId, ObjectModel objectModel) {
		
		if(projectId == null || projectId.equals("")) {
			return "Project ID can't be empty";
		}
		
		if(objectModel.getSwitches().size() + objectModel.getAppliances().size() == 0) {
			return "Project must have at least one router, switch or appliance";
		}
		
		Set<String> interfaces = new HashSet<String>();
		for(Interface interfac : objectModel.getInterfaces()) {
			if(interfac != null && interfaces.contains(interfac.getIpAddress().getAddress())) {
				return "IpAddress " + interfac.getIpAddress().getAddress() + " was duplicated";
				
			} else {
				interfaces.add(interfac.getIpAddress().getAddress());
			}
		}
		
		return checkNameUniqeuness(objectModel);
	}
	
	/**
	 * Checks the name uniqueness among resources, switches and routers
	 * 
	 * @param objectModel
	 * @return String description of error or null otherwise
	 */
	public String checkNameUniqeuness(ObjectModel objectModel) {
		
		Set<String> routers = new HashSet<String>();
		Set<String> appliances = new HashSet<String>();
		Set<String> switches = new HashSet<String>();
		
		for(Appliance app : objectModel.getAppliances()) {
			if(app.getResourceId() == null || app.getResourceId().equals("")) {
				return "ResourceId can't be null";
			}
			if(app.getType().equals(ApplianceType.MACHINE)) {
				if(appliances.contains(app.getResourceId())) {
					return "Duplicate appliance " + app.getResourceId() + " resource name";
				} else {
					appliances.add(app.getResourceId());
				}
				
			} else if(app.getType().equals(ApplianceType.ROUTER)) {
				if(routers.contains(app.getResourceId())) {
					return "Duplicate router " + app.getResourceId() + " resource name";
				} else {
					routers.add(app.getResourceId());
				}
			}
		}
		
		for(Switch switc : objectModel.getSwitches()) {
			
			if(switc.getResourceId() == null || switc.getResourceId().equals("")) {
				return "ResourceId can't be null";
			}
			
			if(switches.contains(switc.getResourceId())) {
				return "Duplicate switch " + switc.getResourceId() + " resource name";
			} else {
				switches.add(switc.getResourceId());
			}
		}
		
		return null;
	}
	
}
