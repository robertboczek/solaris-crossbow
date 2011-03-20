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
	public static void saveNetwork(String selected, List<Object> list) {
		
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(new File(selected)));
			output.writeObject(list);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}

	/**
	 * Loads network from selected file
	 * 
	 * @param selected Selected file path
	 */
	public static List<Object> loadNetwork(String selected) {
		
		ObjectInputStream input = null;
		List<Object> list = null;
		try {
			input = new ObjectInputStream(new FileInputStream(new File(selected)));
			list = (List<Object>) input.readObject();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

}
