package org.jims.common.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jims.agent.exception.CommandException;
import org.jims.common.ManagementCommons;

public abstract class AbstractCommand {

	private final static Logger logger = Logger.getLogger( AbstractCommand.class );	
	
	protected List<String> parseStream( InputStream is )
	{
		InputStreamReader isreader = null;
		BufferedReader bufreader = null;
		
		List<String> ret = new ArrayList<String>(); 		
		try
		{			
			isreader = new InputStreamReader( is );  
			bufreader = new BufferedReader( isreader );
			
			String s;
			while ( (s = bufreader.readLine())!=null )
			{
				ret.add(s.trim());
			}
		}
		catch(IOException iox)
		{
			logger.error( "Solaris10 command failed during read operaion of process-stream:" + iox.getMessage(),iox );
		}
		finally
		{
			if ( is != null ) try { is.close(); } catch(Exception e){}
			if ( bufreader != null ) try { bufreader.close(); } catch(Exception e){}
		}
		
		return ret;
	}
	
	/**
	 * Invokes set of OS commands.
	 *  
	 * @param cmds array containing OS commands to be invoked
	 * @throws CommandException
	 */
	protected void invokeOsCommands(Collection<String[]> cmdcol) throws CommandException
	{
		String[] cmdarray = null;
		try
		{
			for( Iterator it = cmdcol.iterator(); it.hasNext();)
			{
				cmdarray = (String[])it.next();
				logger.info("Invoking command=[" + arrayToString(cmdarray) +"]");
				Process p = Runtime.getRuntime().exec( cmdarray );				
				if ( p.waitFor() == 0 )
				{
					List<String> result = parseStream( p.getInputStream() );
					logger.info("result=[" + result + "]" );				
				}
				else
				{
					String errmsg = "Invoking command=[" + arrayToString(cmdarray) + "] failed (exit value=" + 
									p.exitValue() + ") ." + parseStream( p.getErrorStream() ) ;
					logger.info(errmsg);
					throw new CommandException(errmsg);
				}
			}
		}
		catch(Exception ex)
		{
			String errmsg = "Invoking command=[" + arrayToString(cmdarray) + "] failed." + ex.getMessage();
			logger.error(errmsg,ex);
			throw new CommandException(errmsg,ex);
		}
	}
	
	/**
	 * Invoke OS command.
	 *  
	 * @param cmdarray containing OS command with arguments
	 * @throws CommandException
	 */
	protected List<String> invokeOsCommand(String[] cmdarray) throws CommandException
	{
		List<String> result = new ArrayList<String>();
		try
		{
			logger.info("Invoking command=[" + arrayToString(cmdarray) + "]");

			Process p = Runtime.getRuntime().exec( cmdarray );
			if ( p.waitFor() == 0 )
			{
				result = parseStream( p.getInputStream() );
				logger.info("Command invoked with result=["  + result + "]");				
			}
			else
			{
				String errmsg = "Invoking command=[" + arrayToString(cmdarray) + "] failed (exit value=" + 
				p.exitValue() + ") ." + parseStream( p.getErrorStream() ) ;
						logger.info(errmsg);
				throw new CommandException(errmsg);
			} 
 		}
		catch(Exception ex)
		{
			String errmsg = "Invoking command=[" + arrayToString(cmdarray) + "] failed." + ex.getMessage();
			logger.error(errmsg,ex);
			throw new CommandException(errmsg,ex);
		}
		
		return result;
	}
	
	/**
	 * Calculate path to specified script stored in JIMS tmp directory.
	 * These scripts are extracted from JAR module file to tmp directory. 
	 *  
	 * @param scriptName
	 * @return
	 */
	protected String prepareJimsScriptPath(String scriptName) {
		
		String tmpDir = ManagementCommons.getJimsTemporaryDir(); 
		// in case jims.osinfo.tmpdir is missing
		if ( tmpDir == null ){
			logger.error("jims.osinfo.tmpdir property is null!");
			throw new IllegalStateException("jims.osinfo.tmpdir property is null!");
		}		
		
		return tmpDir + File.separator + scriptName;
	}
		
	/**
	 * 
	 * @param a
	 * @return
	 */
	protected String arrayToString(String[] a) {
		StringBuffer ret = new StringBuffer();
		for (String s:a)
		{
			ret.append(s + " ");
		}
		
		return ret.toString(); 
	}	
}
