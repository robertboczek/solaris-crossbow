package org.jims.modules.crossbow.flow.util;

import org.jims.modules.crossbow.flow.Flow;
import org.jims.modules.crossbow.flow.FlowInfo;


/**
 * Translator class used to transform between FlowInfo and Flow classes.
 *
 * @author cieplik
 */
public class FlowToFlowInfoTranslator {

	/**
	 * @brief  Translates Flow instance to FlowInfo instance.
	 *
	 * @param  flow  Flow instance
	 *
	 * @return  FlowInfo instance
	 */
	public static FlowInfo toFlowInfo( Flow flow ) {

		return new FlowInfo( flow.getName(), flow.getLink(),
		                     flow.getAttrs(), flow.getProps(),
		                     flow.isTemporary() );

	}


	/**
	 * @brief  Translates FlowInfo instance to Flow instance.
	 *
	 * @param   flowInfo  FlowInfo instance
	 *
	 * @return  Flow instance
	 */
	public static Flow toFlow( FlowInfo flowInfo ) {

		Flow flow = new Flow();

		flow.setAttrs( flowInfo.getAttributes() );
		flow.setProps( flowInfo.getProperties() );
		flow.setLink( flowInfo.getLink() );
		flow.setName( flowInfo.getName() );
		flow.setTemporary( flowInfo.isTemporary() );

		return flow;

	}

}
