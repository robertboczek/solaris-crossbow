package org.jims.modules.solaris.solaris10.mbeans;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.jims.agent.exception.JimsManagementException;
import org.jims.common.ManagementCommons;
import org.jims.common.NetworkUtils;
import org.jims.common.fsutils.TailLogReader;
import org.jims.common.fsutils.TailLogReader.LogReaderHandler;
import org.jims.common.fsutils.TailLogReader.ThreadTailLogReader;
import org.jims.model.solaris.solaris10.ProjectInfo;
import org.jims.model.solaris.solaris10.ZoneInfo;
import org.jims.model.solaris.solaris10.rctl.RctlAction;
import org.jims.model.solaris.solaris10.rctl.ResourceControl;
import org.jims.model.solaris.solaris10.rctl.ResourceControl.ControlName;
import org.jims.model.solaris.solaris10.rctl.ResourceControl.EntityType;
import org.jims.model.solaris.solaris10.rctl.ResourceControl.Privilege;
import org.jims.modules.solaris.solaris10.fs.FsMonitor;
import org.jims.modules.solaris.solaris10.fs.FsUtils;
import org.jims.modules.solaris.solaris10.fs.FsMonitor.FileEntry;
import org.jims.modules.solaris.solaris10.fs.FsMonitor.FileListener;
import org.jims.modules.solaris.solaris10.helpers.ResourceControlsManager;
import org.jims.modules.solaris.solaris10.helpers.ZoneManager.ZonesModifyInformation;
import org.jims.modules.solaris.solaris10.mbeans.ZoneAgentMBean.ContainsIpReturnCode;
import org.jims.modules.solaris.solaris10.mbeans.notifications.WnZoneEmitter;


/**
 * Management interface exposed for Global zone.
 * 
 * @author bombel
 * @version $Id: GlobalZoneManagement.java 1529 2006-10-25 14:20:49 +0200 (Wed,
 *          25 Oct 2006) bombel $
 */
public class GlobalZoneManagement extends ZoneManagementCommons implements
		GlobalZoneManagementMBean, FileListener {
	private final static Logger logger = Logger
			.getLogger(GlobalZoneManagement.class);

	private Timer zoneMonitorTimer;

	private FsMonitor fsMonitor;

	private ThreadTailLogReader msgTailThread;

	private Map<String, LocalZoneManagement> zoneMBeans = Collections
			.synchronizedMap(new HashMap<String, LocalZoneManagement>());

	private WnZoneEmitter zoneEmitter;

	protected Solaris10ExactMonitorMBean exacctMonitorMBean;

	/**
	 * Default constructor which initializes this MBean
	 * 
	 * @throws JimsManagementException
	 */
	public GlobalZoneManagement() throws JimsManagementException {
		initialize();
	}

	/**
	 * Check if specified IP is assigned to any zone installed within this host.
	 * 
	 * @throws JimsManagementException
	 */
	public ContainsIpReturnCode containsIP(String ipAddress)
			throws JimsManagementException {
		try {
			if (NetworkUtils.isHostIPAddress(ipAddress))
				return ContainsIpReturnCode.CURRENT_ZONE_CONTAINS;

			for (LocalZoneManagement lzm : zoneMBeans.values()) {
				if (lzm.containsIP(ipAddress) == ContainsIpReturnCode.CURRENT_ZONE_CONTAINS)
					return ContainsIpReturnCode.GLOBAL_CONTAINS_IN_LOCAL;
			}

			return ContainsIpReturnCode.DOES_NOT_CONTAIN;
		} catch (Exception e) {
			throw new JimsManagementException(e);
		}
	}

	public void bootZone(String zoneName) throws JimsManagementException {
		LocalZoneManagement zoneMBean = zoneMBeans.get(zoneName);
		if (zoneMBean != null) {
				zoneMBean.bootZone();
		} else {
			throw new JimsManagementException(
					"Zone management interface couldn't be found!");
		}
	}

	public void readyZone(String zoneName) throws JimsManagementException {
		LocalZoneManagement zoneMBean = zoneMBeans.get(zoneName);
		if (zoneMBean != null) {
			zoneMBean.readyZone();
		} else {
			throw new JimsManagementException(
					"Zone management interface couldn't be found!");
		}
	}

	public void rebootZone(String zoneName) throws JimsManagementException {
		LocalZoneManagement zoneMBean = zoneMBeans.get(zoneName);
		if (zoneMBean != null) {
			zoneMBean.rebootZone();
		} else {
			throw new JimsManagementException(
					"Zone management interface couldn't be found!");
		}
	}

	public void shutdownZone(String zoneName) throws JimsManagementException {
		LocalZoneManagement zoneMBean = zoneMBeans.get(zoneName);
		if (zoneMBean != null) {
			zoneMBean.shutdownZone();
		} else {
			throw new JimsManagementException(
					"Zone management interface couldn't be found!");
		}
	}

	/**
	 * 
	 */
	public void createZone(ZoneInfo zoneInfo) {
	}

	public void createZoneFromSnapshot(ZoneInfo zoneInfo, String srcSnapshot) 
		throws JimsManagementException {
		
		try {
			zoneManager.createZoneFromSnapshot(zoneInfo, srcSnapshot);
			logger.info("Zone created. Registering zone MBean components ...");
			addZoneMBean(zoneInfo);
			resMonitor.addZoneMBean(zoneInfo);
			logger.info( "MBean components registered!" );
		} catch (Exception e) {
			logger.error("Zone creation process from snapshot failed:" + e.getMessage(), e);
			throw new JimsManagementException("Zone creation process from snapshot failed:" + e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 */
	public void removeZone(String name) {

	}

	/**
	 * 
	 */
	public void getZoneInfo(String name) {

	}

	/**
	 * 
	 */
	public void modifyZone(ZoneInfo zoneInfo) {
	}

	/**
	 * 
	 */
	public List getZones() {

		List< String > zones = new LinkedList< String >();

		try {

			for ( ZoneInfo zi : zoneManager.getZones() ) {
				zones.add( zi.getName() );
			}

		} catch ( JimsManagementException ex ) {
			// java.util.logging.Logger.getLogger( GlobalZoneManagement.class.getName() ).log( Level.SEVERE, null, ex );
		}

		return zones;

	}

	/**
	 * @param fileEntry
	 * 
	 */
	public void fileChanged(FileEntry fileEntry) {
		Object root = fileEntry.getRootObject();
		if (root instanceof ZoneInfo) {
			ZoneInfo zone = (ZoneInfo) root;
			logger.info("Refreshing projects database for zone="
					+ zone.getName());
			if ("global".equals(zone.getName())) {
				try {
					refreshProjects();
				} catch (Exception ex) {
					logger.error("Refreshing zone projects failed:"
							+ ex.getMessage(), ex);
				}
			} else {
				LocalZoneManagement zoneMBean = zoneMBeans.get(zone.getName());
				if (zoneMBean != null) {
					try {
						zoneMBean.refreshProjects();
					} catch (Exception ex) {
						logger.error("Refreshing zone projects failed:"
								+ ex.getMessage() + " ,zoneName="
								+ zone.getName(), ex);
					}
				}
			}
		}
	}

	/**
	 * Initializes MBean, creates others Projects MBeans, starts various
	 * monitors:
	 * <li> "/etc/project" database for each running zone,
	 * <li> zones state monitor,
	 * <li> "/var/adm/messages" in which, resource controls messages about
	 * defined actions are logged e.g. threshold for resource control was
	 * exceeded
	 * 
	 * @throws JimsManagementException
	 */
	protected void initialize() throws JimsManagementException {
		super.initialize();

		zoneInfo = zoneManager.getCurrentZone();

		fsMonitor = FsMonitor.getInstance();

		// init projects MBeans, projects DB monitor
		initProjectsMBeans();

		// init local zones MBeans
		initLocalZonesMBeans();

		registerWnZonesEmitterMBean();

		registerResourceMonitorMBean();
		for (LocalZoneManagement zoneMBean : zoneMBeans.values()) {
			zoneMBean.setResMonitor(resMonitor);
		}

		// If configured, register solaris Accounting mbean
		String isAccoutingActived = SolarisManagementCommons
				.getProperty("jims.agent.mbean.exacctmonitor.active");
		if ((new Boolean(isAccoutingActived).booleanValue())) {
			registerSolaris10AccountingMBean();
		}
		
		fsMonitor.addListener(this);
		fsMonitor.start(ZONE_PROJECTS_MONITOR_INTERVAL);

		zoneMonitorTimer = new Timer(true);
		zoneMonitorTimer.schedule(new TimerTask() {
			public void run() {
				fireCheckZoneModification();
			}
		}, 10000, ZONE_MONITOR_INTERVAL);

		msgTailThread = TailLogReader.tail("/var/adm/messages",
				new LogReaderHandler() {
					public void handleNewContent(String[] lines) {
						logsChanged(lines);
					}

					public void restart() {
						restartLogsReader();
					}
				}, 1000);
	}

	/**
	 * Invoked by the
	 * {@link} LogReaderHandler#handleNewContent when /var/adm/messages 
	 * changes. It delegates to the <code>ResourceControlsManager#logsChanged.   
	 * 
	 * @param lines new content in the monitored file
	 */
	private void logsChanged(String[] lines) {
		try {
			List<RctlAction> actions = rctlManager.logsChanged(lines);
			logger.info("resource action=" + actions);
		} catch (JimsManagementException jme) {
			logger.error("Post-processing content of /var/adm/messages failed:"
					+ jme.getMessage(), jme);
		}
	}

	private void restartLogsReader() {

	}

	/**
	 * Init MBeans for projects for current global zone
	 * 
	 * @throws JimsManagementException
	 */
	private void initProjectsMBeans() throws JimsManagementException {
		List<ProjectInfo> projects = projectManager.getProjects();

		for (ProjectInfo pinfo : projects) {
			addProjectMBean(pinfo);
		}

		// create projects DB monitor
		fsMonitor.addFile(new FileEntry(FsUtils.PROJECTS_CFG_FILE, zoneInfo));
	}

	/**
	 * Inits MBeans for local zones management, projects mbeans are initialized
	 * in local zone management mbean.
	 * 
	 * @see org.jims.modules.solaris.solaris10.mbeans.LocalZoneManagement
	 * 
	 * @throws JimsManagementException
	 */
	private void initLocalZonesMBeans() throws JimsManagementException {
		try {
			MBeanServer mbs = ManagementCommons.getMBeanServer();
			List<ZoneInfo> zones = zoneManager.getZones();
			for (ZoneInfo zone : zones) {
				addZoneMBean(zone, mbs);
			}
		} catch (Exception e) {
			String errmsg = "Initialization of local zones mbeans failed:"
					+ e.getMessage();
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg, e);
		}
	}

	/**
	 * 
	 * @param zoneInfo
	 * @throws JimsManagementException
	 */
	private void addZoneMBean(ZoneInfo zone) throws JimsManagementException {
		MBeanServer mbs = ManagementCommons.getMBeanServer();
		this.addZoneMBean(zone, mbs);
	}

	/**
	 * 
	 * @param zoneInfo
	 * @param mbs
	 *            mBeanServer in which Local zone MBean is to be registered
	 * 
	 * @see org.jims.modules.solaris.solaris10.mbeans.LocalZoneManagement
	 * 
	 * @throws JimsManagementException
	 */
	private void addZoneMBean(ZoneInfo zone, MBeanServer mbs)
			throws JimsManagementException {
		try {
			ObjectName beanname = createZoneManagementObjectName(zone.getName());
			LocalZoneManagement mBean = new LocalZoneManagement(zone);
			mbs.registerMBean(mBean, beanname);
			zoneMBeans.put(zone.getName(), mBean);
			logger.info(beanname + " registered!");
			
			if (zone.isGlobal()) {
				fsMonitor
						.addFile(new FileEntry(FsUtils.PROJECTS_CFG_FILE, zone));
			} else {
				fsMonitor.addFile(new FileEntry(zone.getZonepath()
						+ FsUtils.LOCAL_ZONE_PROJECTS_CFG_FILE, zone));
			}

		} catch (Exception e) {
			String errmsg = "Initialization of local zone mbean failed:"
					+ e.getMessage() + " zone=" + zone.getName();
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg, e);
		}
	}

	/**
	 * Checks for zones modification
	 */
	private void fireCheckZoneModification() {
		try {
			logger.info( "zones modification monitor fired!" );
			
			ZonesModifyInformation info = zoneManager.checkZonesModification();

			if (info.getAdded().size() > 0) {
				getZoneEmitter().sendZonesAddedNotification(info.getAdded());
			}

			if (info.getUpdated().size() > 0) {
				getZoneEmitter()
						.sendZonesUpdatedNotification(info.getUpdated());
			}

			if (info.getRemoved().size() > 0) {
				getZoneEmitter()
						.sendZonesRemovedNotification(info.getRemoved());
			}
		} catch (JimsManagementException e) {
			logger.error("fireCheckZoneModification exception:"
					+ e.getMessage(), e);
		}
	}

	/**
	 * Registers Exacct mbean manager. This MBean should be only registered in
	 * Global zone environment.
	 * 
	 * @throws JimsManagementException
	 * @throws IllegalStateException
	 *             if non global zone environment
	 */
	private void registerSolaris10AccountingMBean()
			throws JimsManagementException {
		if (!zoneManager.isGlobalZone()) {
			throw new IllegalStateException(
					"Solaris Accounting mbean should be only registered within global zone!");
		}

		String exacctMBeanName = assembleSolaris10ExacctMonitorName();
		try {
			ObjectName exacctName = new ObjectName(exacctMBeanName);

			String logDirPath = getProperty("jims.agent.logdir.path");

			exacctMonitorMBean = new Solaris10ExactMonitor(logDirPath);
			ManagementCommons.getMBeanServer().registerMBean(exacctMonitorMBean, exacctName);
		} catch (NullPointerException e) {
			String msg = "Internal error: ObjectName for Solaris10ExactMonitor MBean is null.";
			logger.error(msg + ": " + e);
			throw new JimsManagementException(msg, e);
		} catch (InstanceAlreadyExistsException e) {
			String errmsg = "Trying to register Solaris10ExactMonitor, InstanceExists!";
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		} catch (MBeanRegistrationException e) {
			String errmsg = "Mbean already exists, name=" + exacctMBeanName;
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		} catch (NotCompliantMBeanException e) {
			String errmsg = "Solaris10ExactMonitor Mbean is not compilant, name="
					+ exacctMBeanName;
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		} catch (MalformedObjectNameException e) {
			String errmsg = "Solaris10ExactMonitor Mbean name is malformed, name="
					+ exacctMBeanName;
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		} catch (NumberFormatException e) {
			String errmsg = "Solaris10ExactMonitor Mbean timer period time has incorrect format";
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		}
	}

	public void registerWnZonesEmitterMBean() throws JimsManagementException {
		String zoneEmmiterName = assembleZonesEmitterMBeanName();

		try {
			ObjectName emitterName = new ObjectName(zoneEmmiterName);
			zoneEmitter = new WnZoneEmitter();
			ManagementCommons.getMBeanServer().registerMBean(zoneEmitter, emitterName);
		} catch (NullPointerException e) {
			String msg = "Internal error: ObjectName for Zones Emmiter MBean is null.";
			logger.error(msg + ": " + e);
			throw new JimsManagementException(msg, e);
		} catch (InstanceAlreadyExistsException e) {
			String errmsg = "Trying to register Zones EmitterMBean, InstanceExists!";
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		} catch (MBeanRegistrationException e) {
			String errmsg = "Mbean already exists, name=" + zoneEmmiterName;
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		} catch (NotCompliantMBeanException e) {
			String errmsg = "Mbean is not compilant, name=" + zoneEmmiterName;
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		} catch (MalformedObjectNameException e) {
			String errmsg = "Mbean name is malformed, name=" + zoneEmmiterName;
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		}
	}

	private WnZoneEmitter getZoneEmitter() throws JimsManagementException {
		if (zoneEmitter == null)
			throw new JimsManagementException("Zone Emitter is not registered!");
		return zoneEmitter;
	}

	public void enableLocalZoneResourceControl(String zone, ResourceControl rc)
			throws JimsManagementException {
		ResourceControlsManager.getInstance().enableResourceControl(zone, rc,
				false);
	}

	public void replaceLocalZoneResourceControl(String zone, ResourceControl rc)
			throws JimsManagementException {
		ResourceControlsManager.getInstance().enableResourceControl(zone, rc,
				true);

	}

	public void disableLocalZoneResourceControl(String zone, ResourceControl rc)
			throws JimsManagementException {
		ResourceControlsManager.getInstance().disableResourceControl(zone, rc,
				false);
	}

	public void deleteLocalZoneResourceControl(String zone, ResourceControl rc)
			throws JimsManagementException {
		ResourceControlsManager.getInstance().disableResourceControl(zone, rc,
				true);
	}

	public void clearLocalZoneResourceControl(String zone,
			ControlName ctrlName, Privilege privilege, BigInteger value,
			EntityType type, int id) throws JimsManagementException {
		ResourceControlsManager.getInstance().clearResourceControl(zone,
				ctrlName, privilege, value, type, id);
	}

	public List<ResourceControl> readLocalZoneResourceControls(
			String zone, EntityType type, int id)
			throws JimsManagementException {
		return ResourceControlsManager.getInstance().getResourceControls(zone,
				type, id);
	}
}
