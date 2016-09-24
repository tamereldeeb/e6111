package com.group11proj1;

import com.group11proj1.services.BingService;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Arrays;

public class Proj1 { 
	
   public static void main(String[] args) {

	   	String query = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");
		BingService service = new BingService(args[0], args[1], query);
	   	Writer writer = null;

	   	try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("transcript.txt"), "utf-8"));
			writer.write(System.lineSeparator());

			Boolean run;
			do {
				run = service.call(writer);
			} while (run);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (writer != null) { writer.close(); }
			} catch (Exception ex) {/*ignore*/}
		}
   }
   
}