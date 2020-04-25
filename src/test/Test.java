package test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import parser.ParserException;
import parser.Stream;
import parser.token.Token;
import parser.token.node.TokenBase;
import parser.token.node.TokenBlock;
import parser.token.node.TokenIdentifier;
import parser.token.node.TokenScope;
import value.ValueDefaultEnv;
import value.effect.Runtime;

public class Test {
	public static void parseTest (File file) throws IOException {
		parseTest(new TokenBase(new Stream(new FileInputStream(file))));
	}
	
	public static void parseTest (TokenBlock scope) throws IOException {
		Token[] tokens = scope.getElements();
		
		TokenScope testCase = null;
		String testExpect = null;
		
		for (int i = 0; i < tokens.length; i += 2) {
			Token tname = tokens[i + 0];
			Token body = tokens[i + 1];
			
			String name;
			
			if (tname instanceof TokenIdentifier) {
				name = ((TokenIdentifier) tname).getIdentifier();
			}else{
				throw new RuntimeException("First token in pair must be an identifier");
			}
			
			if (name.equals("test")) {
				parseTest((TokenBlock) body);
			}else if (name.equals("case")) {
				if (testCase != null) throw new RuntimeException("there can only be one case defined per test");
				testCase = new TokenScope((TokenBlock) body);
			}else if (name.equals("expect")) {
				if (testExpect != null) throw new RuntimeException("there can only be one expected output defined per test");
				
				testExpect = ((TokenIdentifier) body).getIdentifier();
			}else {
				throw new ParserException("Unknown directive: " + name);
			}
		}
		
		if (testCase != null || testExpect != null) {
			if (testCase == null || testExpect == null) {
				throw new ParserException("Test must specify both a test case and the result of the execution");
			}
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			Runtime r = new Runtime(new PrintStream(bytes));
			ValueDefaultEnv.run(r, testCase.createNode());
			
			String computed = Charset.forName("UTF8").decode(ByteBuffer.wrap(bytes.toByteArray())).toString();
			
			if (testExpect.equals(computed)) {
				System.out.println("Test passed");
			}else {
				System.out.println("Test failed");
				System.out.println(computed);
				System.out.println(testExpect);
			}
		}
	}
	
	public static void test (File directory) throws IOException{
		File[] files = directory.listFiles();
		
		for (File f : files) {
			if (f.isDirectory()) {
				test(f);
			}else {
				parseTest(f);
			}
		}
	}
}
