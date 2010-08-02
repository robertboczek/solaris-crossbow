package agh.msc.xbowbase.flow.util;

import agh.msc.xbowbase.flow.Flow;
import agh.msc.xbowbase.flow.FlowInfo;


/**
 *
 * @author cieplik
 */
public class FlowToFlowInfoTranslator {

	public static FlowInfo toFlowInfo( Flow flow ) {

		return new FlowInfo( flow.getName(), flow.getLink(),
		                     flow.getAttrs(), flow.getProps(),
		                     flow.isTemporary() );

	}


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
