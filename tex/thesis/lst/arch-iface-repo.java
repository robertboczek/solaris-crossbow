public interface RepoManagerMBean {

	/**
	 * Retrieves IDs of all appliances registered in the repository.
	 */
	public List< String > getIds();

	/**
	 * Returns filesystem path of the repository.
	 */
	public String getRepoPath();

	/**
	 * Sets the respository filesystem path.
	 */
	public void setRepoPath( String path );

}
