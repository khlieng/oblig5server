package sample.hello;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContext implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		Users.connect("localhost/oblig5", "root", "");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
	}
}
