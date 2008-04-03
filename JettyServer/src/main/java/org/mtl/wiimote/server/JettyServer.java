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

		connector.setPort(8080);
        threadPool.setMaxThreads(100);
        server.setThreadPool(threadPool);
		server.setConnectors(new Connector[] {connector});

		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream("server.properties"));
			String war  = prop.getProperty("target.war");
			String ctxt = prop.getProperty("target.contextpath");
			
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
