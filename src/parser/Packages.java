package parser;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import event.Event;
import event.EventScope;
import parser.token.Token;
import value.Value;

public class Packages {
	public static Value load (ByteBuffer buf) {
		Token ast = Parser.parse(buf);
		//System.out.println(ast);
		
		Event event = new EventScope(ast.createEvent());
		event.init();
		event.paramaterHeight(new Node<Integer>(0), new Node<Integer>(0));
		Value v = event.run(Global.global);
		return v;
	}
	
	public static Value file (String path) {
		System.out.println("Loading: " + path);
		
		try {
			return Packages.load(ByteBuffer.wrap(Files.readAllBytes(new File(path).toPath())));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Value getPackage (String name) {
		try {
			InputStream stream = Packages.class.getClassLoader().getResourceAsStream("ace/" + name);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = stream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			
			return Packages.load(ByteBuffer.wrap(out.toByteArray()));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
