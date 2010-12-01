package org.jims.modules.crossbow.jna;

import org.jims.modules.crossbow.exception.IncompatibleFlowException;
import org.jims.modules.crossbow.exception.NoSuchEnumException;
import org.jims.modules.crossbow.exception.NoSuchFlowException;
import org.jims.modules.crossbow.exception.ValidationException;
import org.jims.modules.crossbow.exception.XbowException;
import org.jims.modules.crossbow.flow.FlowInfo;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import org.jims.modules.crossbow.jna.mapping.FlowHandle;
import org.jims.modules.crossbow.jna.util.MapToKeyValuePairsTranslator;
import org.jims.modules.crossbow.lib.FlowHelper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Flow helper implementation based on Java Native Access.
 *
 * @author cieplik
 */
public class JNAFlowHelper implements FlowHelper {

    /**
     * Creates the helper object and initializes underlying handler.
     */
    public JNAFlowHelper() {

        String location = System.getProperty(JIMS_HOME_PROP) + File.separator + JIMS_LIBDIR + LIB_NAME;
        handle = (FlowHandle) Native.loadLibrary(location, FlowHandle.class);
        handle.init();

    }

    /**
     * Creates the helper object using user-provided JNA handle.
     *
     * @param  handle  JNA handle
     */
    public JNAFlowHelper(FlowHandle handle) {
        this.handle = handle;
    }

    /**
     * @see  FlowHelper#remove(java.lang.String, boolean)
     */
    @Override
    public void remove(String flow, boolean temporary) throws XbowException,
            NoSuchFlowException {

        int rc = handle.remove_flow(flow, temporary);

        logger.debug("remove_flow returned with rc == " + rc + " .");

        // If remove_flow failed, map rc to exception and throw it.

        if (rc != XbowStatus.XBOW_STATUS_OK.ordinal()) {

            if (XbowStatus.XBOW_STATUS_NOTFOUND.ordinal() == rc) {
                throw new NoSuchFlowException(flow);
            } else {
                throw new XbowException("Could not remove " + flow);
            }
        }

    }

    /**
     * @see  FlowHelper#getAttributes(java.lang.String)
     */
    @Override
    public Map<FlowAttribute, String> getAttributes(String flowName) throws NoSuchFlowException {

        for (FlowInfo flowInfo : getFlowsInfo()) {
            if (flowInfo.getName().equals(flowName)) {
                return flowInfo.getAttributes();
            }
        }

        // The flow could not be found - raise exception.

        throw new NoSuchFlowException(flowName);

    }

    /**
     * @see  FlowHelper#setProperties(java.lang.String, java.util.Map, boolean)
     */
    @Override
    public void setProperties(String flowName, Map<FlowProperty, String> properties, boolean temporary)
            throws ValidationException,
            XbowException {

        // Call set_property sequentially, each time setting single property.

        for (Map.Entry<FlowProperty, String> entry : properties.entrySet()) {

            String values[] = entry.getValue().split(",");

            int rc = handle.set_property(flowName, entry.getKey().toString(), values, values.length, temporary);

            logger.debug("set_property returned with rc == " + rc + " .");

            // Check the rc and map it to exception, if necessary.

            if (rc != XbowStatus.XBOW_STATUS_OK.ordinal()) {

                if (rc == XbowStatus.XBOW_STATUS_PROP_PARSE_ERR.ordinal()) {
                    throw new ValidationException(entry.getKey() + "=" + values);
                } else {
                    throw new XbowException(String.valueOf(rc));
                }

            }

        }

    }

    /**
     * @see  FlowHelper#getProperties(java.lang.String)
     */
    @Override
    public Map<FlowProperty, String> getProperties(String flowName) throws NoSuchFlowException {

        // TODO-DAWID: rc z helpera (obsluga sytuacji, gdy flow nie istnieje)

        // Query the library.

        FlowHandle.KeyValuePairsStruct kvps = handle.get_properties(flowName);

        Map<FlowProperty, String> properties = new HashMap<FlowProperty, String>();

        try {

            for (Map.Entry<FlowProperty, String> entry : MapToKeyValuePairsTranslator.toPropMap(kvps.fill()).entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }

        } catch (NoSuchEnumException e) {
            logger.error("Could not find mapping for property", e);
        }

        // Free the memory.

        handle.free_key_value_pairs(kvps);

        return properties;

    }

    /**
     * @see  FlowHelper#resetProperties(java.lang.String, java.util.List, boolean)
     */
    @Override
    public void resetProperties(String flowName, List<FlowProperty> properties, boolean temporary)
            throws NoSuchFlowException,
            ValidationException {

        // Reset properties sequentially.

        for (FlowProperty property : properties) {

            int rc = handle.reset_property(flowName, property.toString(), temporary);

            logger.debug("reset_property returned with rc == " + rc);

            // Check the rc and map it to exception, if necessary.

            if (XbowStatus.XBOW_STATUS_NOTFOUND.ordinal() == rc) {

                throw new NoSuchFlowException(flowName);

            } else if (XbowStatus.XBOW_STATUS_PROP_PARSE_ERR.ordinal() == rc) {

                throw new ValidationException(property.toString());

            }

        }

    }

    /**
     * @see  FlowHelper#create(agh.msc.xbowbase.flow.FlowInfo)
     */
    @Override
    public void create(FlowInfo flowInfo) throws IncompatibleFlowException,
            XbowException {

        int rc = handle.create(new FlowHandle.FlowInfoStruct(flowInfo), flowInfo.isTemporary());

        logger.debug("create returned with rc == " + rc + " .");

        if (rc != XbowStatus.XBOW_STATUS_OK.ordinal()) {

            if (rc == XbowStatus.XBOW_STATUS_FLOW_INCOMPATIBLE.ordinal()) {
                throw new IncompatibleFlowException(flowInfo.getName());
            } else {
                throw new XbowException("Creation failed.");
            }

        }

    }

    /**
     * @see  FlowHelper#getFlowsInfo()
     */
    @Override
    public List<FlowInfo> getFlowsInfo() {
        return getFlowsInfo(null);
    }

    /**
     * @see  FlowHelper#getFlowsInfo(java.util.List)
     */
    @Override
    public List<FlowInfo> getFlowsInfo(List<String> links) {

        List<FlowInfo> res = new LinkedList<FlowInfo>();

        // Call helper function.

        FlowHandle.FlowInfosStruct flowInfosStruct = handle.get_flows_info(
                (null == links) ? null : (String[]) links.toArray());

        logger.debug("get_flows_info returned " + flowInfosStruct.flowInfosLen + " FlowInfoStruct(s).");

        // Process returned structs.

        for (Pointer p : flowInfosStruct.flowInfos.getPointerArray(0, flowInfosStruct.flowInfosLen)) {

            FlowHandle.FlowInfoStruct struct = new FlowHandle.FlowInfoStruct(p);

            // Append to the resulting list.

            try {

                res.add(new FlowInfo(
                        struct.name,
                        struct.link,
                        MapToKeyValuePairsTranslator.toAttrMap(struct.attrs.fill()),
                        MapToKeyValuePairsTranslator.toPropMap(struct.props.fill()),
                        struct.temporary));

            } catch (NoSuchEnumException e) {
                logger.error("Could not find mapping for enum.", e);
            }

        }

        // Free the memory.

        handle.free_flow_infos(flowInfosStruct);

        return res;

    }
    public final static String JIMS_HOME_PROP = "jims_home";
    public final static String JIMS_LIBDIR = "share" + File.separator + "java" + File.separator;
    private final String LIB_NAME = "libjims-crossbow-native-lib-flow-3.0.0.so";
    FlowHandle handle = null;
    private static final Logger logger = Logger.getLogger(JNAFlowHelper.class);
}
