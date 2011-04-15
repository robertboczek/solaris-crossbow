package org.jims.modules.crossbow.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;
import org.jims.modules.crossbow.gui.data.GraphConnectionData;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Actions.ACTION;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Endpoint;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;

public class NetworkStructureHelper {

	private static final Logger logger = Logger
			.getLogger(NetworkStructureHelper.class);

	private NetworkState networkState;

	private Map<Object, GraphItem> newObjects = new HashMap<Object, GraphItem>();
	private Map<Object, GraphItem> deployedObjects = new HashMap<Object, GraphItem>();
	private Map<Object, GraphItem> modifiedObjects = new HashMap<Object, GraphItem>();
	private Map<Object, GraphItem> removedObjects = new HashMap<Object, GraphItem>();

	private Set<Interface> newInterfaces = new HashSet<Interface>();
	private Set<Policy> newPolicies = new HashSet<Policy>();
	private Set<Interface> modifiedInterfaces = new HashSet<Interface>();
	private Set<Policy> modifiedPolicies = new HashSet<Policy>();

	private Graph graph;
	private Text projectId;
	
	public NetworkStructureHelper(Graph graph, Text projectId) {
		this.graph = graph;
		this.projectId = projectId;
	}

	public NetworkState getNetworkState() {
		return networkState;
	}

	public void setNetworkState(NetworkState networkState) {
		this.networkState = networkState;
	}

	public void deployed() {

		this.networkState = NetworkState.DEPLOYED;
		for (Map.Entry<Object, GraphItem> entry : newObjects.entrySet()) {
			deployedObjects.put(entry.getKey(), entry.getValue());
		}
		
		for (Map.Entry<Object, GraphItem> entry : modifiedObjects.entrySet()) {
			deployedObjects.put(entry.getKey(), entry.getValue());
		}
		
		newObjects.clear();
		removedObjects.clear();
		modifiedObjects.clear();

		newInterfaces.clear();
		newPolicies.clear();
		modifiedInterfaces.clear();
		modifiedPolicies.clear();
	}

	public void addItem(NetworkType networkType) {

		GraphNode graphNode = null;
		String iconFileName = null;
		Object obj = null;

		if (networkType.equals(NetworkType.SWITCH)) {
			iconFileName = "icons/switch.jpg";
			Switch swit = new Switch("", "");
			obj = swit;
		} else if (networkType.equals(NetworkType.MACHINE)) {
			iconFileName = "icons/resource.jpg";
			Appliance appliance = new Appliance("", "", ApplianceType.MACHINE);
			obj = appliance;
		} else if (networkType.equals(NetworkType.ROUTER)) {
			iconFileName = "icons/router.jpg";
			Appliance appliance = new Appliance("", "", ApplianceType.ROUTER);
			obj = appliance;
		}

		if (obj != null) {
			graphNode = createGraphItem(obj, iconFileName);
			newObjects.put(obj, graphNode);
		}

	}

	public void addDeployedElement(Object obj, GraphItem graphItem) {
		deployedObjects.put(obj, graphItem);
	}

	public void removeItems(List<Object> objects) {

		// najpierw iteruje po wszystkich GraphConnection
		for (Object obj : objects) {
			if (obj instanceof GraphConnection) {
				GraphConnection gc = (GraphConnection) obj;
				gc.setVisible(false);
				GraphConnectionData gcd = (GraphConnectionData) gc.getData();

				removeConnection(gcd);
			}
		}

		// potem po wszystkich GraphNode'ach
		for (Object obj : objects) {
			if (obj instanceof GraphNode) {
				Object objectToRemove = ((GraphItem) obj).getData();

				((GraphItem) obj).setVisible(false);
				if (newObjects.containsKey(objectToRemove)) {
					newObjects.remove(objectToRemove);
				} else if (deployedObjects.containsKey(objectToRemove)) {
					removedObjects.put(objectToRemove, deployedObjects
							.get(objectToRemove));
				} else if (modifiedObjects.containsKey(objectToRemove)) {
					removedObjects.put(objectToRemove, modifiedObjects
							.get(objectToRemove));
				}

				removeElement(objectToRemove);
			}

		}
	}

	/**
	 * Usuwa element Switch, Router lub MAchine i aktualizuje endpointy
	 * 
	 * @param objectToRemove
	 */
	private void removeElement(Object objectToRemove) {

		if (objectToRemove instanceof Appliance) {
			Appliance appliance = (Appliance) objectToRemove;
			for (Interface interfac : appliance.getInterfaces()) {
				removeReferenceToEndpoint(interfac.getEndpoint(), interfac);
			}

		} else if (objectToRemove instanceof Switch) {
			Switch switc = (Switch) objectToRemove;
			for (Endpoint endpoint : switc.getEndpoints()) {
				removeReferenceToEndpoint(endpoint, switc);
			}
		}

		if (modifiedObjects.containsKey(objectToRemove)
				|| deployedObjects.containsKey(objectToRemove)) {
			modifiedObjects.remove(objectToRemove);
			deployedObjects.remove(objectToRemove);
			removedObjects.put(objectToRemove, null);
		}

	}

	private void removeReferenceToEndpoint(Endpoint endpoint, Object obj) {

		for (Map.Entry<Object, GraphItem> entry : newObjects.entrySet()) {

			if (entry.getKey().equals(endpoint)) {
				Object object = entry.getKey();
				if (object instanceof Appliance) {
					Appliance appliance = (Appliance) object;
					for (Interface interfac : appliance.getInterfaces()) {
						if (interfac.getEndpoint().equals(object)) {
							interfac.setEndpoint(null);
						}
					}

				} else if (object instanceof Switch) {
					Switch switc = (Switch) object;
					switc.getEndpoints().remove(obj);
				}
			}
		}
	}

	private void removeConnection(GraphConnectionData gcd) {

		removeReferenceFromNodeElement(gcd.getLeftNode(), gcd.getEndp2());
		removeReferenceFromNodeElement(gcd.getRightNode(), gcd.getEndp1());

	}

	private void removeReferenceFromNodeElement(Object node, Endpoint endp) {

		if (node instanceof Appliance) {
			Appliance appliance = (Appliance) node;
			for (Interface interfac : appliance.getInterfaces()) {
				interfac.setEndpoint(null);
			}

		} else if (node instanceof Switch) {
			Switch switc = (Switch) node;
			switc.getEndpoints().remove(endp);
		}
	}

	protected GraphNode createGraphItem(Object g, String iconPath) {

		logger.trace("Creating new graph item");
		
		// By default, put the item directly into the graph.
		
		IContainer container = graph;
		
		if ( ( 1 == graph.getSelection().size() )
		     && ( graph.getSelection().get( 0 ) instanceof GraphContainer ) ) {
			
			// If, however, a container is selected, put the item into it.
			container = ( GraphContainer ) graph.getSelection().get( 0 );
			
		}

		GraphNode graphNode = new GraphNode( container, SWT.NONE, "");
		graphNode.setImage(loadImage(iconPath));
		graphNode.setData(g);
		String toolTipText = updateGraphNodeToolTip(g);
		graphNode.setTooltip(new org.eclipse.draw2d.Label(toolTipText));
		
		if (g instanceof Appliance) {
			Appliance appliance = (Appliance) g;
			if (projectId.getText() == null || projectId.getText().equals("")) {
				projectId.setText(appliance.getProjectId());
			}
		}

		graphNode.setText(toolTipText);

		return graphNode;

	}

	protected Image loadImage(String fileName) {

		ImageDescriptor descriptor = ImageDescriptor.createFromFile(null,
				fileName);
		return descriptor.createImage();
	}

	protected String updateGraphNodeToolTip(Object obj) {

		logger.debug("Updating tool tip for object " + obj);

		StringBuilder sb = new StringBuilder();
		if (obj instanceof Appliance) {

			Appliance appliance = (Appliance) obj;
			if (appliance.getType() != null
					&& appliance.getType().equals(ApplianceType.MACHINE)) {
				sb.append("RepoId: ");
				if (appliance.getRepoId() != null)
					sb.append(appliance.getRepoId());
			}
			sb.append("\nResourceId: ");
			if (appliance.getResourceId() != null)
				sb.append(appliance.getResourceId());
		} else if (obj instanceof Switch) {

			Switch swit = (Switch) obj;
			sb.append("\nResourceId: ");
			if (swit.getResourceId() != null)
				sb.append(swit.getResourceId());
		}
		sb.append("\n");
		if (obj instanceof Appliance) {
			Appliance appliance = (Appliance) obj;
			for (Interface inter : appliance.getInterfaces()) {
				if (inter.getIpAddress() != null) {
					sb.append(inter.getIpAddress().toString() + "\n");
				}
			}
		}

		return sb.toString();
	}

	public void connect(GraphNode graphNode, GraphNode graphNode2,
			Endpoint rightEndpoint, Endpoint leftEndpoint) {

		Object obj = graphNode.getData();
		if (obj instanceof Appliance) {
			for (Interface interf : ((Appliance) obj).getInterfaces()) {
				if (interf.equals(rightEndpoint)) {
					logger.info("Added new endpoint " + leftEndpoint
							+ " to interface " + interf);
					interf.setEndpoint(leftEndpoint);
				}
			}
		} else if (obj instanceof Switch) {
			logger.info("Added new endpoint " + leftEndpoint + " to switch "
					+ obj);
			((Switch) obj).getEndpoints().add(leftEndpoint);
		}

		obj = graphNode2.getData();
		if (obj instanceof Appliance) {
			for (Interface interf : ((Appliance) obj).getInterfaces()) {
				if (interf.equals(leftEndpoint)) {
					logger.info("Added new endpoint " + rightEndpoint
							+ " to interface " + interf);
					interf.setEndpoint(rightEndpoint);
				}
			}
		} else if (obj instanceof Switch) {
			logger.info("Added new endpoint " + rightEndpoint + " to switch "
					+ obj);
			((Switch) obj).getEndpoints().add(rightEndpoint);
		}
	}

	public void clearAllItems() {

		networkState = NetworkState.UNDEPLOYED;

		newObjects.clear();
		deployedObjects.clear();
		modifiedObjects.clear();
		removedObjects.clear();
		
		modifiedInterfaces.clear();
		modifiedPolicies.clear();
		newInterfaces.clear();
		newPolicies.clear();

		hideItems();
	}

	private void hideItems() {

		logger.trace("Hiding all items");

		for (Object object : graph.getConnections())
			((GraphConnection) object).setVisible(false);
		for (Object object : graph.getNodes())
			((GraphItem) object).setVisible(false);
	}

	public Set<Object> getNewObjects() {
		return newObjects.keySet();
	}

	public Set<Object> getDeployedObjects() {
		return deployedObjects.keySet();
	}

	public Set<Object> getChangedObjects() {
		return modifiedObjects.keySet();
	}

	public Set<Object> getRemovedObjects() {
		return removedObjects.keySet();
	}

	public String getProjectId() {
		return projectId.getText();
	}

	private ACTION getAction(Object obj) {
		if (newObjects.containsKey(obj)) {
			return Actions.ACTION.ADD;
		} else if (deployedObjects.containsKey(obj)) {
			return Actions.ACTION.NOOP;
		} else if (modifiedObjects.containsKey(obj)) {
			return Actions.ACTION.UPD;
		} else if (removedObjects.containsKey(obj)) {
			return Actions.ACTION.REM;
		}
		return Actions.ACTION.NOOP;
	}

	/**
	 * Zwraca ACTION w zaleznosci w ktorej mapie sie znajduje appliance app
	 * 
	 * @param app
	 * @return Zwraca odpowiednia akcje do wykonania
	 */
	public ACTION getApplianceAction(Appliance app) {
		return getAction(app);
	}

	/**
	 * Zwraca ACTION w zaleznosci w ktorej mapie sie znajduje sie app
	 * 
	 * @param app
	 * @return Zwraca odpowiednia akcje do wykonania
	 */
	public ACTION getSwitchAction(Switch swit) {
		return getAction(swit);
	}

	public ACTION getPolicyAction(Policy policy) {

		if (hasPolicy(policy, newObjects)) {
			return Actions.ACTION.ADD;
		} else if (hasPolicy(policy, removedObjects)) {
			return Actions.ACTION.REM;
		} else if (newPolicies.contains(policy)) {
			return Actions.ACTION.ADD;
		} else if (modifiedPolicies.contains(policy)) {
			return Actions.ACTION.UPD;
		}

		return Actions.ACTION.NOOP;
	}

	public ACTION getInterfaceAction(Interface interf) {

		if (hasInterface(interf, newObjects)) {
			return Actions.ACTION.ADD;
		} else if (hasInterface(interf, removedObjects)) {
			return Actions.ACTION.REM;
		} else if (newInterfaces.contains(interf)) {
			return Actions.ACTION.ADD;
		} else if (modifiedInterfaces.contains(interf)) {
			return Actions.ACTION.UPD;
		}

		return Actions.ACTION.NOOP;
	}

	private boolean hasPolicy(Policy policy, Map<Object, GraphItem> objectsMap) {

		for (Map.Entry<Object, GraphItem> entry : objectsMap.entrySet()) {

			Object object = entry.getKey();
			if (object instanceof Appliance) {
				Appliance appliance = (Appliance) object;
				for (Interface interfac : appliance.getInterfaces()) {
					if (interfac.getPoliciesList().contains(policy)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean hasInterface(Interface interf,
			Map<Object, GraphItem> objectsMap) {

		for (Map.Entry<Object, GraphItem> entry : objectsMap.entrySet()) {

			Object object = entry.getKey();
			if (object instanceof Appliance) {
				Appliance appliance = (Appliance) object;
				if (appliance.getInterfaces().contains(interf)) {
					return true;
				}
			}
		}
		return false;

	}

	/**
	 * Actions dla elementu Object ma byc Actions.UPD
	 * 
	 * @param object
	 */
	public void updateElement(Object object) {

		if (object instanceof Appliance || object instanceof Switch) {

			if (deployedObjects.containsKey(object)) {
				modifiedObjects.put(object, deployedObjects.get(object));
				deployedObjects.remove(object);
			}

		} else if (object instanceof Policy) {

			modifiedPolicies.add((Policy) object);

		} else if (object instanceof Interface) {

			modifiedInterfaces.add((Interface) object);

		}
	}

	/**
	 * Dodaje nowy element do stworzenia w jimsie
	 * 
	 * @param object
	 *            Nowo utworzony obiekt Interface lub Policy
	 */
	public void addNewElement(Object object) {

		if (object instanceof Policy) {
			logger.debug("Added new policy to policies list");
			newPolicies.add((Policy) object);
		} else if (object instanceof Interface) {
			logger.debug("Added new policy to interface list");
			newInterfaces.add((Interface) object);
		}
	}
}

enum NetworkState {
	DEPLOYED, UNDEPLOYED
}
