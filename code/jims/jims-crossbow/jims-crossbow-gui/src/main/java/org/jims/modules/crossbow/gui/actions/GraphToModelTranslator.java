package org.jims.modules.crossbow.gui.actions;

import java.util.List;

import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;

public class GraphToModelTranslator {
	
	public ObjectModel translate(List<Object> objects) {
		
		ObjectModel objectModel = new ObjectModel();
		registerObjects(objectModel, objects);
		
		return objectModel;
	}
	
	protected void registerObjects(ObjectModel objectModel, List<Object> objects) {

		for (Object obj : objects) {
			if (obj instanceof Switch) {
				Switch swit = (Switch) obj;
				objectModel.register(swit);
			} else if (obj instanceof Appliance) {
				Appliance app = (Appliance) obj;
				objectModel.register(app);

				int ifaceNo = 0;
				for (Interface interf : app.getInterfaces()) {
					interf.setResourceId("IFACE" + ifaceNo);
					objectModel.register(interf);
					for (Policy policy : interf.getPoliciesList()) {
						objectModel.register(policy);
					}

					++ifaceNo;
				}
			}
		}

	}

	public void updateProjectIdName(List<Object> modelObjects, String projectId) {
		
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


}
