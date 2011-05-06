package org.jims.modules.crossbow.gui.actions;

import java.util.HashSet;
import java.util.Set;

import org.jims.modules.crossbow.gui.NetworkStructureHelper;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;

public class GraphToModelTranslator {
	
	public ObjectModel translate(NetworkStructureHelper networkStructureHelper) {
		
		return registerObjects(networkStructureHelper);
	}
	
	protected ObjectModel registerObjects(NetworkStructureHelper networkStructureHelper) {
		
		ObjectModel objectModel = new ObjectModel();

		Set<Object> set = new HashSet<Object>();
		set.addAll(networkStructureHelper.getChangedObjects());
		set.addAll(networkStructureHelper.getNewObjects());
		set.addAll(networkStructureHelper.getDeployedObjects());
		set.addAll(networkStructureHelper.getRemovedObjects());
		
		for (Object obj : set) {
			if (obj instanceof Switch) {
				Switch swit = (Switch) obj;
				objectModel.register(swit);
			} else if (obj instanceof Appliance) {
				Appliance app = (Appliance) obj;
				objectModel.register(app);

				int ifaceNo = 0;
				for (Interface interf : app.getInterfaces()) {
					interf.setResourceId("IFACE" + ifaceNo);
					//System.err.println("Policies number " + interf.getPoliciesList().size());
					objectModel.register(interf);
					for (Policy policy : interf.getPoliciesList()) {
						objectModel.register(policy);
					}
					++ifaceNo;
				}
			}
		}
		
		return objectModel;
	}

	public void updateProjectIdName(Set<Object> modelObjects, String projectId) {
		
		for (Object obj : modelObjects) {
			if (obj instanceof Switch) {
				Switch swit = (Switch) obj;
				swit.setProjectId(projectId);
			} else if (obj instanceof Appliance) {
				Appliance app = (Appliance) obj;
				app.setProjectId(projectId);
				for (Interface interf : app.getInterfaces()) {
					interf.setProjectId(projectId);
				}
			}
		}
	}
	
	public Actions createActions(ObjectModel om, NetworkStructureHelper networkStructureHelper) {
		
		Actions actions = new Actions();

		for (Appliance app : om.getAppliances()) {
			actions.put(app, networkStructureHelper.getApplianceAction(app));
		}
		for (Interface interf : om.getInterfaces()) {
			actions.put(interf, networkStructureHelper.getInterfaceAction(interf));
		}
		for (Switch swit : om.getSwitches()) {
			actions.put(swit, networkStructureHelper.getSwitchAction(swit));
		}
		for (Policy policy : om.getPolicies()) {
			actions.put(policy, networkStructureHelper.getPolicyAction(policy));
		}

		return actions;
	}

	public void updateProjectIdName(
			NetworkStructureHelper networkStructureHelper) {
		
		updateProjectIdName(networkStructureHelper.getChangedObjects(), networkStructureHelper.getProjectId());
		updateProjectIdName(networkStructureHelper.getNewObjects(), networkStructureHelper.getProjectId());
		updateProjectIdName(networkStructureHelper.getDeployedObjects(), networkStructureHelper.getProjectId());
		updateProjectIdName(networkStructureHelper.getRemovedObjects(), networkStructureHelper.getProjectId());
	}


}
