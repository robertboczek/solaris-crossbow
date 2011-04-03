package org.jims.modules.crossbow.infrastructure.appliance;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author cieplik
 */
public class RepoManager implements RepoManagerMBean {

	@Override
	public List< String > getIds() {

		List< String > res = new LinkedList< String >();

		File root = new File( repoPath );
		for ( String filename : root.list() ) {

			Matcher m = p.matcher( filename );

			if ( m.matches() && ( ! res.contains( m.group( 1 ) ) ) ) {
				res.add( m.group( 1 ) );
			}

		}

		return res;

	}


	@Override
	public String getRepoPath() {
		return repoPath;
	}

	@Override
	public void setRepoPath( String path ) {
		this.repoPath = path;
	}


	private String repoPath = "/appliance";

	private Pattern p = Pattern.compile( "(\\w+)((\\.ROOT(\\.zbe)?)?\\.SNAP)" );

}
