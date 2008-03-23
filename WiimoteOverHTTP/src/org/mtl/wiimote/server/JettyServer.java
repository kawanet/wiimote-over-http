package org.mtl.wiimote.server;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.BoundedThreadPool;

public class JettyServer{
	public static void main(String[] args) throws Exception {
		Server server = new Server();
	    BoundedThreadPool threadPool = new BoundedThreadPool();
		SelectChannelConnector connector = new SelectChannelConnector();

		connector.setPort(8080);
        threadPool.setMaxThreads(100);
        server.setThreadPool(threadPool);
		server.setConnectors(new Connector[] {connector});

		WebAppContext web = new WebAppContext();
		web.setWar("WebContent");
		server.addHandler(web);

		try{
			server.start();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
}
