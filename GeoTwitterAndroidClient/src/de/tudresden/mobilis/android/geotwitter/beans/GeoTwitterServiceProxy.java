package de.tudresden.mobilis.android.geotwitter.beans;

import java.util.List;import java.util.ArrayList;public class GeoTwitterServiceProxy {

	private IGeoTwitterServiceOutgoing _bindingStub;


	public GeoTwitterServiceProxy( IGeoTwitterServiceOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public IGeoTwitterServiceOutgoing getBindingStub(){
		return _bindingStub;
	}


}