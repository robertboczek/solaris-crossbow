package org.jims.modules.crossbow.gui.actions;

import java.util.HashSet;
import java.util.List;
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
		
		ObjectModel objectModel = new ObjectModel();
		registerObjects(objectModel, networkStructureHelper);
		
		return objectModel;
	}
	
	protected void registerObjects(ObjectModel objectModel, NetworkStructureHelper networkStructureHelper) {

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
					System.err.println("Policies number " + interf.getPoliciesList().size());
					objectModel.register(interf);
					for (Policy policy : interf.getPoliciesList()) {
						objectModel.register(policy);
					}

					++ifaceNo;
				}
			}
		}
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
			actions.insert(app, networkStructureHelper.getAction(app));
		}
		for (Interface interf : om.getPorts()) {
			actions.insert(interf, Actions.ACTION.ADD);
		}
		for (Switch swit : om.getSwitches()) {
			actions.insert(swit, Actions.ACTION.ADD);
		}
		for (Policy policy : om.getPolicies()) {
			actions.insert(policy, Actions.ACTION.ADD);
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
