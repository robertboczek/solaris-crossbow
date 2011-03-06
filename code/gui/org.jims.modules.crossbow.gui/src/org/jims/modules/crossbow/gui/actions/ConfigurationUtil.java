package org.jims.modules.crossbow.gui.actions;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.jims.modules.crossbow.gui.data.GraphNodeData;

/**
 * Saves and loads network configuration to file
 * 
 * @author robert
 *
 */
public class ConfigurationUtil extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		return null;
	}

	/**
	 * Saves network configuration fo file
	 *  
	 * @param selected Selected file path
	 * @param graphNodesDataList Items to be saved
	 */
	public static void saveNetwork(String selected, List<GraphNodeData> graphNodesDataList) {
		
		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(selected)));
			output.writeInt(graphNodesDataList.size());
			for(GraphNodeData graphNodeData : graphNodesDataList){
				output.writeObject(graphNodeData);
			}
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Loads network from selected file
	 * 
	 * @param selected Selected file path
	 * @param graphNodesDataList Items where to read
	 */
	public static void loadNetwork(String selected, List<GraphNodeData> graphNodesDataList) {
		try {
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(selected)));
			int i = input.readInt();
			System.out.println(i);
			for(int j = 0; j<i; j++)
				graphNodesDataList.add((GraphNodeData)input.readObject());
			
			input.close();
		} catch (EOFException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
