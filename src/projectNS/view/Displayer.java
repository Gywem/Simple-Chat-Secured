package projectNS.view;

import java.util.Iterator;
import java.util.List;

import projectNS.persistance.model.AppMSG;

public class Displayer {
	
	public static Displayer Singleton = Displayer.getInstance();
	
	private Displayer() {
		
	}

	public static Displayer getInstance() {
		if(Singleton == null) {
			return new Displayer();
		} else {
			return Singleton;
		}
	}
	
	public void displayAppMsg(AppMSG msg) {
		System.out.println("("+msg.getClientId()+"): "+msg.getBody());
	}

	public void displayMsgExchangeSection(List<AppMSG> appMSGs, String clientID, String server) {
		Displayer.clearConsole();
		this.displayAppMessages(appMSGs);
		
		System.out.println("Type the message for sending to "+clientID+": ");
		System.out.print("I am ("+server+"): ");
	}

	public void displayAppMessages(List<AppMSG> appMSGs) {
		Iterator<AppMSG> it = appMSGs.iterator();
		
		while(it.hasNext()) {
			AppMSG appmsg = it.next();			
			this.displayAppMsg(appmsg);			
		}		
	}
	

	
	public final static void clearConsole()
	{
	    try
	    {
	        final String os = System.getProperty("os.name");

	        if (os.contains("Windows"))
	        {
	            Runtime.getRuntime().exec("cls");
	        }
	        else
	        {
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (final Exception e)
	    {
	        //  Handle any exceptions.
	    }
	}

}
