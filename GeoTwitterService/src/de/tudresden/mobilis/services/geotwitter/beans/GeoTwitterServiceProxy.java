package de.tudresden.mobilis.services.geotwitter.beans;

public class GeoTwitterServiceProxy {

	private IGeoTwitterServiceOutgoing _bindingStub;


	public GeoTwitterServiceProxy( IGeoTwitterServiceOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public IGeoTwitterServiceOutgoing getBindingStub(){
		return _bindingStub;
	}


	public void pushNewTreasure( String toJid, Treasure Treasure ) {
		if ( null == _bindingStub )
			return;

		pushNewTreasure out = new pushNewTreasure( Treasure );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out );
	}

}