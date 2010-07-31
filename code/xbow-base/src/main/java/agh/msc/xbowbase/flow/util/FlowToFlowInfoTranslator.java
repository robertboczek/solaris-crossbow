package agh.msc.xbowbase.flow.util;

import agh.msc.xbowbase.flow.Flow;
import agh.msc.xbowbase.flow.FlowInfo;


/**
 *
 * @author cieplik
 */
public class FlowToFlowInfoTranslator {

	// TODO-DAWID: write tests

	public static FlowInfo toFlowInfo( Flow flow ) {

		FlowInfo flowInfo = new FlowInfo();

		flowInfo.name = flow.getName();
		flowInfo.link = flow.getLink();
		flowInfo.attributes = flow.getAttributes();
		flowInfo.properties = flow.getProperties();
		flowInfo.temporary = flow.isTemporary();

		return flowInfo;

	}


	public static Flow toFlow( FlowInfo flowInfo ) {

		Flow flow = new Flow();

		flow.setAttrs( flowInfo.attributes );
		flow.setProps( flowInfo.properties );
		flow.setLink( flowInfo.getLink() );
		flow.setName( flowInfo.getName() );
		flow.setTemporary( flowInfo.temporary );

		return flow;

	}

}
