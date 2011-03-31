package org.jims.modules.crossbow.gui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.jims.modules.crossbow.gui.data.GraphConnectionData;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Endpoint;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;


public class ModelToGraphTranslator {
	
	public enum Element {
		GRAPH_NODE,
		GRAPH_EDGE
	}
	
	// TODO-DAWID equals+hashcode
	
	public void translate( Graph graph, ObjectModel om ) {
		
		Map< Object, GraphNode > nodes = new HashMap< Object, GraphNode >();
		
		for ( Appliance app : om.getAppliances() ) {
			
			if ( ApplianceType.MACHINE.equals( app.getType() ) ) {
				createGraphItem( graph, app, "icons/resource.jpg", nodes );
			} else if ( ApplianceType.ROUTER.equals( app.getType() ) ) {
				createGraphItem( graph, app, "icons/router.jpg", nodes );
			}
			
		}
		
		for ( Switch s : om.getSwitches() ) {
			createGraphItem( graph, s, "icons/switch.jpg", nodes );
		}

		restoreGraphNodeConnections( graph, om, nodes );
		graph.applyLayout();
		
	}
	
	
	public void setColor( Element element, Color color ) {
		colors.put( element, color );
	}
	
	
	private void createGraphItem( Graph graph, Object g, String iconPath,
	                              Map< Object, GraphNode > nodes ) {

		GraphNode graphNode = new GraphNode(graph, SWT.NONE, "");
		graphNode.setBackgroundColor( colors.get( Element.GRAPH_NODE ) );
				
		graphNode.setImage(loadImage(iconPath));
		graphNode.setData(g);
		String toolTipText = updateGraphNodeToolTip(g);
		graphNode.setTooltip(new org.eclipse.draw2d.Label(toolTipText));

		// if (g instanceof Appliance) {
			// Appliance appliance = (Appliance) g;
			
			// if (projectId.getText() == null || projectId.getText().equals("")) {
				// projectId.setText(appliance.getProjectId());
			// }
		// }

		graphNode.setText(toolTipText);
		
		nodes.put( g, graphNode );

	}
	
	
	private Image loadImage(String fileName) {

		ImageDescriptor descriptor = ImageDescriptor.createFromFile(null, fileName);
		return descriptor.createImage();
		
	}
	
	
	private String updateGraphNodeToolTip( Object obj ) {

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
	
	
	protected void restoreGraphNodeConnections( Graph graph, ObjectModel model,
	                                            Map< Object, GraphNode > nodes ) {
		
		for ( Appliance app : model.getAppliances() ) {
			
			for ( Interface iface : app.getInterfaces() ) {
				
				createGraphConnectionData(
					graph,
					nodes.get( app ), nodes.get( iface.getEndpoint() ),
					iface, iface.getEndpoint()
				);
				
			}
			
		}

	}
	
	
	private void createGraphConnectionData( Graph graph,
	                                        GraphNode graphNode, GraphNode graphNode2,
	                                        Endpoint endp1, Endpoint endp2 ) {

		System.err.println(graphNode + " restore " + graphNode2);
		if (graphNode == null || graphNode2 == null) {
			return;
		}
		GraphConnectionData graphConnectionData = new GraphConnectionData(
				graphNode.getData(), graphNode2.getData(), endp1, endp2);

		System.err.println(endp1 + " restore " + endp2);

		// graphConnectionDataList.add(graphConnectionData);

		GraphConnection graphConnection = new GraphConnection(graph, SWT.NONE,
				graphNode, graphNode2);
		graphConnection.setData(graphConnectionData);
		graphConnection.setLineWidth(2);
		graphConnection.setLineColor( colors.get( Element.GRAPH_EDGE ) );
		
		// updateGraphConnection = true;

	}
	
	
	Map< Element, Color > colors = new HashMap< Element, Color >();
	
}
