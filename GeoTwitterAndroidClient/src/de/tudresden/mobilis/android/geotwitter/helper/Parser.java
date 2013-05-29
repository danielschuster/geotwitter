package de.tudresden.mobilis.android.geotwitter.helper;

import java.io.StringReader;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.mobilis.android.geotwitter.beans.createTreasureResponse;
import de.tudresden.mobilis.android.geotwitter.beans.getTreasureContentResponse;
import de.tudresden.mobilis.android.geotwitter.beans.pushNewTreasure;
import de.tudresden.mobilis.android.geotwitter.beans.sendTreasureList;


public class Parser {

	public static XMPPIQ beanToIQ(XMPPBean bean, boolean mergePayload) {
		// default XMPP IQ type
		int type = XMPPIQ.TYPE_GET;
		
		switch (bean.getType()) {
			case XMPPBean.TYPE_GET:    type = XMPPIQ.TYPE_GET; break;
			case XMPPBean.TYPE_SET:    type = XMPPIQ.TYPE_SET; break;
			case XMPPBean.TYPE_RESULT: type = XMPPIQ.TYPE_RESULT; break;
			case XMPPBean.TYPE_ERROR:  type = XMPPIQ.TYPE_ERROR; break;
		}
		
		XMPPIQ iq;
		
		if (mergePayload)
			iq = new XMPPIQ( bean.getFrom(), bean.getTo(), type, null, null, bean.toXML() );
		else
			iq = new XMPPIQ( bean.getFrom(), bean.getTo(), type,
					bean.getChildElement(), bean.getNamespace(), bean.payloadToXML() );
		
		iq.packetID = bean.getId();
		
		return iq;
	}
	
	/**
	 * Formats a Bean to a string.
	 *
	 * @param bean the bean
	 * @return the formatted string of the Bean
	 */
	public String beanToString(XMPPBean bean){
		String str = "XMPPBean: [NS="
			+ bean.getNamespace()
			+ " id=" + bean.getId()
			+ " from=" + bean.getFrom()
			+ " to=" + bean.getTo()
			+ " type=" + bean.getType()
			+ " payload=" + bean.payloadToXML();
		
		if(bean.errorCondition != null)
			str += " errorCondition=" + bean.errorCondition;
		if(bean.errorText != null)
			str += " errorText=" + bean.errorText;
		if(bean.errorType != null)
			str += " errorType=" + bean.errorType;
		
		str += "]";
		
		return str;
	}
	
	/**
	 * Convert XMPPIQ to XMPPBean to simplify the handling of the IQ using the 
	 * beanPrototypes.
	 *
	 * @param iq the XMPPIQ
	 * @return the related XMPPBean or null if something goes wrong
	 */
	public static XMPPBean convertXMPPIQToBean(XMPPIQ iq) {
		
		try {
			String childElement = iq.element;
			String namespace    = iq.namespace;
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(iq.payload));
			XMPPBean bean = null;
			
			
			if(childElement.equals(createTreasureResponse.CHILD_ELEMENT)&&namespace.equals(createTreasureResponse.NAMESPACE)){
				bean = new createTreasureResponse();
			}
			if(childElement.equals(getTreasureContentResponse.CHILD_ELEMENT)&&namespace.equals(getTreasureContentResponse.NAMESPACE)){
				bean = new getTreasureContentResponse();
			}
			if(childElement.equals(sendTreasureList.CHILD_ELEMENT)&&namespace.equals(sendTreasureList.NAMESPACE)){
				bean = new sendTreasureList();
			}
			if(childElement.equals(pushNewTreasure.CHILD_ELEMENT)&&namespace.equals(pushNewTreasure.NAMESPACE)){
				bean = new pushNewTreasure();
			}
			
					bean.fromXML(parser);
					
					bean.setId(iq.packetID);
					bean.setFrom(iq.from);
					bean.setTo(iq.to);
					
					switch (iq.type) {
						case XMPPIQ.TYPE_GET: bean.setType(XMPPBean.TYPE_GET); break;
						case XMPPIQ.TYPE_SET: bean.setType(XMPPBean.TYPE_SET); break;
						case XMPPIQ.TYPE_RESULT: bean.setType(XMPPBean.TYPE_RESULT); break;
						case XMPPIQ.TYPE_ERROR: bean.setType(XMPPBean.TYPE_ERROR); break;
					}
					
					return bean;
				
			
		} catch (Exception e) {
			
		}
		
		return null;
	}
}
