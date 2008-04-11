package org.mtl.wiimote.server;

import java.io.FileInputStream;
import java.util.Properties;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.BoundedThreadPool;

public class JettyServer{
	public static void main(String[] args){
		Server server = new Server();
	    BoundedThreadPool threadPool = new BoundedThreadPool();
		SelectChannelConnector connector = new SelectChannelConnector();

		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("server.properties"));
			String port = prop.getProperty("server.port");
			String ctxt = prop.getProperty("target.contextpath");
			String war  = prop.getProperty("target.war");
	
			connector.setPort(Integer.parseInt(port));
	        threadPool.setMaxThreads(100);
	        server.setThreadPool(threadPool);
			server.setConnectors(new Connector[] {connector});

		
//			WebAppContext web = new WebAppContext(".", ctxt);
						
			WebAppContext web = new WebAppContext();
			web.setWar(war);
			web.setContextPath("/"+ctxt);
			
			server.addHandler(web);

			server.start();
			server.join();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
}
