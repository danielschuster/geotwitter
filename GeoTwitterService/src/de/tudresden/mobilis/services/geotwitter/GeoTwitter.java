package de.tudresden.mobilis.services.geotwitter;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;

import de.tudresden.inf.rn.mobilis.server.services.MobilisService;
import de.tudresden.inf.rn.mobilis.xmpp.beans.ProxyBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanProviderAdapter;
import de.tudresden.mobilis.services.geotwitter.beans.GeoTwitterServiceProxy;
import de.tudresden.mobilis.services.geotwitter.beans.IGeoTwitterServiceOutgoing;
import de.tudresden.mobilis.services.geotwitter.beans.IXMPPCallback;
import de.tudresden.mobilis.services.geotwitter.beans.createTreasureRequest;
import de.tudresden.mobilis.services.geotwitter.beans.createTreasureResponse;
import de.tudresden.mobilis.services.geotwitter.beans.getTreasureContentRequest;
import de.tudresden.mobilis.services.geotwitter.beans.getTreasureContentResponse;
import de.tudresden.mobilis.services.geotwitter.beans.pushNewTreasure;
import de.tudresden.mobilis.services.geotwitter.beans.sendTreasureList;
import de.tudresden.mobilis.services.geotwitter.beans.updateLocation;
import de.tudresden.mobilis.services.geotwitter.helpers.BeanProcessor;
import de.tudresden.mobilis.services.geotwitter.helpers.SqlHelper;

/**
 * @author MARCHELLO, Marian Seliuchenko
 * Email: egur2006@yandex.ru
 * Last modified: 15.05.2013
 */

public class GeoTwitter extends MobilisService  {

	private GeoTwitterServiceProxy _proxy;
	private DoubleKeyMap< String, String, XMPPBean > _beanPrototypes
	= new DoubleKeyMap< String, String, XMPPBean >( false );

	private BeanProcessor beanProcessor;
	private Timer timer;
	private SqlHelper db;
	FileTransferManager fm;


	public GeoTwitter(){
		_proxy = new GeoTwitterServiceProxy(IGeoTwitterServiceOutgoingStub);
		db = new SqlHelper();
		beanProcessor = new BeanProcessor(this);
		timer = new Timer();
		TimerTask task = new TimerTask() {
			int i = 0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				db.UpdateOnlineUserList();
			}
		};
		timer.schedule(task, 10000, 30000);

	}

	public SqlHelper getDB(){
		return this.db;
	}


	private IGeoTwitterServiceOutgoing IGeoTwitterServiceOutgoingStub = new IGeoTwitterServiceOutgoing(){

		@Override
		public void sendXMPPBean(XMPPBean out) {
			// TODO Auto-generated method stub
			getAgent().getConnection().sendPacket(new BeanIQAdapter(out));

		}

		@Override
		public void sendXMPPBean(XMPPBean out,
				IXMPPCallback<? extends XMPPBean> callback) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	protected void registerPacketListener() {
		// TODO Auto-generated method stub
		_beanPrototypes.put( createTreasureRequest.NAMESPACE, createTreasureRequest.CHILD_ELEMENT, new createTreasureRequest() );
		_beanPrototypes.put( createTreasureResponse.NAMESPACE, createTreasureResponse.CHILD_ELEMENT, new createTreasureResponse() );
		_beanPrototypes.put( getTreasureContentRequest.NAMESPACE, getTreasureContentRequest.CHILD_ELEMENT, new getTreasureContentRequest() );
		_beanPrototypes.put( getTreasureContentResponse.NAMESPACE, getTreasureContentResponse.CHILD_ELEMENT, new getTreasureContentResponse() );
		_beanPrototypes.put( updateLocation.NAMESPACE, updateLocation.CHILD_ELEMENT, new updateLocation() );
		_beanPrototypes.put( sendTreasureList.NAMESPACE, sendTreasureList.CHILD_ELEMENT, new sendTreasureList() );
		_beanPrototypes.put( pushNewTreasure.NAMESPACE, pushNewTreasure.CHILD_ELEMENT, new pushNewTreasure() );

		for ( XMPPBean prototype : _beanPrototypes.getListOfAllValues() ) {
			( new BeanProviderAdapter( 
					new ProxyBean( prototype.getNamespace(), prototype.getChildElement() ) ) ).addToProviderManager();
		}
		IQListener iqListener = new IQListener();
		PacketTypeFilter locFil = new PacketTypeFilter(IQ.class);
		getAgent().getConnection().addPacketListener(iqListener, locFil);

	}



	private class IQListener implements PacketListener {
		@Override
		public void processPacket(Packet packet){
			if(packet instanceof BeanIQAdapter){
				XMPPBean inBean = ((BeanIQAdapter)packet).getBean();
				if(inBean instanceof ProxyBean){
					ProxyBean proxyBean = (ProxyBean)inBean; 



					if(proxyBean.isTypeOf(createTreasureRequest.NAMESPACE, createTreasureRequest.CHILD_ELEMENT)){
						createTreasureRequest request = (createTreasureRequest)proxyBean.parsePayload(new createTreasureRequest());
						createTreasureResponse response = beanProcessor.processCreateTreasureRequest(request);
						response.setTo(request.getFrom());
						response.setFrom(getAgent().getJid());
						_proxy.getBindingStub().sendXMPPBean(response);



					}
					if(proxyBean.isTypeOf(getTreasureContentRequest.NAMESPACE, getTreasureContentRequest.CHILD_ELEMENT)){
						getTreasureContentRequest request = (getTreasureContentRequest)proxyBean.parsePayload(new getTreasureContentRequest());
						getTreasureContentResponse response = beanProcessor.processgetTreasureContentRequest(request);
						response.setTo(request.getFrom());
						response.setFrom(getAgent().getJid());
						_proxy.getBindingStub().sendXMPPBean(response);
					}
					if(proxyBean.isTypeOf(updateLocation.NAMESPACE, updateLocation.CHILD_ELEMENT)){
						updateLocation request = (updateLocation)proxyBean.parsePayload(new updateLocation());
						sendTreasureList response = beanProcessor.processUpdateLocation(request);
						response.setTo(request.getFrom());
						response.setFrom(getAgent().getJid());
						_proxy.getBindingStub().sendXMPPBean(response);
						db.setUserOnline(request.getFrom());
					}
				}
			}

		};

	}



}