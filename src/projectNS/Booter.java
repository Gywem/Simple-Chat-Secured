/*
 * Project Network Security
 * author: Ignacio José Codoñer Gil (igcogi@gmail.com)
 *  - st number : 0416040
 * */

package projectNS;

import projectNS.controller.AppController;
import projectNS.controller.CAController;

public class Booter {
	public static AppController appController;
	public static CAController cAController;
	
	public static void main(String[] args) {
		if(args[0].equals("node")) {
			System.out.println("Communication node");
			appController = AppController.getInstance();
			appController.load();			
		} else if(args[0].equals("ca")) {
			System.out.println("Certification authority");
			cAController = CAController.getInstance();
			cAController.load();
		}
		
		
	}
}
