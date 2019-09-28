package parser;
import java.io.File;

import event.EventFunction;

public class Main {
	public static File path;
	
	public static void main(String[] args) {
		path = new File(args[0]).getParentFile();
		Packages.file(args[0]);
		
		System.out.println("\nmem: " + EventFunction.scopes + ":" + EventFunction.mem);
	}
}
