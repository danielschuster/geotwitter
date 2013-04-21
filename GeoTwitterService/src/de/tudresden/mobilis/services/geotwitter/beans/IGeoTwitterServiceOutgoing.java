package de.tudresden.mobilis.services.geotwitter.beans;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IGeoTwitterServiceOutgoing {

	void sendXMPPBean( XMPPBean out, IXMPPCallback< ? extends XMPPBean > callback );

	void sendXMPPBean( XMPPBean out );

}