import services.BingService;

import java.io.IOException;

public class Proj1 { 
	
   public static void main(String[] args) { 
	
		System.out.println("Parameters:");
		System.out.println("Client key: " + args[0]);
		System.out.println("Query: " + args[2]);
		System.out.println("Precision: " + args[1]);
		
		BingService service = new BingService(args[0], args[1], args[2]);
		
		try {
			service.call();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
   }
   
}