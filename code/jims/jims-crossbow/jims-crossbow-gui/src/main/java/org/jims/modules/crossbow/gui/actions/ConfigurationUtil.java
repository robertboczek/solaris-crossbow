package org.jims.modules.crossbow.gui.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.jims.modules.crossbow.gui.data.NetworkInfo;

/**
 * Saves and loads network configuration to file
 * 
 * @author robert
 * 
 */
public class ConfigurationUtil extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		return null;
	}

	/**
	 * Saves network configuration to file
	 * 
	 * @param selected
	 *            Selected file path
	 * @param networkInfo
	 */
	public static void saveNetwork(String selected, NetworkInfo networkInfo) {

		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(new File(
					selected)));
			output.writeObject(networkInfo);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
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
	 * @param selected
	 *            Selected file path
	 */
	public static NetworkInfo loadNetwork(String selected) {

		ObjectInputStream input = null;
		NetworkInfo networkInfo = null;
		try {
			input = new ObjectInputStream(new FileInputStream(
					new File(selected)));

			networkInfo = (NetworkInfo) input.readObject();

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

		return networkInfo;
	}
}
