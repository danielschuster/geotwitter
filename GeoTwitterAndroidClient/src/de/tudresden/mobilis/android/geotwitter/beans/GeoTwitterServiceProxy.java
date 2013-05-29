package de.tudresden.mobilis.android.geotwitter.beans;public class GeoTwitterServiceProxy {

	private IGeoTwitterServiceOutgoing _bindingStub;


	public GeoTwitterServiceProxy( IGeoTwitterServiceOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public IGeoTwitterServiceOutgoing getBindingStub(){
		return _bindingStub;
	}


}