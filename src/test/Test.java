package test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import parser.Color;
import parser.ParserException;
import parser.Stream;
import parser.token.Token;
import parser.token.node.TokenBase;
import parser.token.node.TokenBlock;
import parser.token.node.TokenIdentifier;
import parser.token.node.TokenScope;
import parser.token.syntax.TokenString;
import value.ValueDefaultEnv;
import value.effect.Runtime;
import value.node.Node;

public class Test {
	private final String name;
	private final Node body;
	private final String expected;
	
	public Test (String name, Node body, String expected) {
		this.name = name;
		this.body = body;
		this.expected = expected;
	}
	
	public String getName() {
		return name;
	}
	
	public String run () {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		Runtime r = new Runtime(new PrintStream(bytes));
		ValueDefaultEnv.run(r, this.body);
		
		String computed = Charset.forName("UTF8").decode(ByteBuffer.wrap(bytes.toByteArray())).toString();
		
		if (this.expected.equals(computed)) {
			return null;
		}else {
			return "Failed test: " + new TokenString(this.getName()).toString() + " got:\n" + computed + "\nbut expected:\n" + this.expected;
		}
	}
	
	public static Test[] parseTest (File file) throws IOException {
		return parseTest(file.getName(), new TokenBase(new Stream(new FileInputStream(file))));
	}
	
	public static Test[] parseTest (String defName, TokenBlock scope) throws IOException {
		Token[] tokens = scope.getElements();
		
		TokenScope testCase = null;
		String testExpect = null;
		String testName = null;
		
		TestList list = new TestList();
		
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
				list.push(parseTest(testName == null ? defName : testName, (TokenBlock) body));
			}else if (name.equals("case")) {
				if (testCase != null) throw new ParserException("there can only be one case defined per test");
				testCase = new TokenScope((TokenBlock) body);
			}else if (name.equals("expect")) {
				if (testExpect != null) throw new ParserException("there can only be one expected output defined per test");
				
				testExpect = ((TokenIdentifier) body).getIdentifier();
			}else if (name.equals("name")) {
				if (testName != null) throw new ParserException("A name has already been defined for the test: " + testName);
				
				testName = ((TokenIdentifier) body).getIdentifier();
			}else {
				throw new ParserException("Unknown directive: " + name);
			}
		}
		
		if (testCase != null || testExpect != null) {
			if (testCase == null || testExpect == null) {
				throw new ParserException("Test must specify both a test case and the result of the execution");
			}
			
			list.push(new Test(testName == null ? defName : testName, testCase.createNode(), testExpect));
		}
		
		return list.toArray();
	}
	
	public static Test[] directory (File directory) throws IOException{
		File[] files = directory.listFiles();
		
		TestList list = new TestList();
		
		for (File f : files) {
			if (f.isDirectory()) {
				list.push(directory(f));
			}else {
				list.push(parseTest(f));
			}
		}
		
		return list.toArray();
	}
	
	public static void test (Test[] tests) {
		int passed = 0, failed = 0;
		StringBuilder failMessage = new StringBuilder();
		
		for (int i = 0; i < tests.length; i++) {
			String test = tests[i].run();
			
			if (test == null) {
				passed++;
			}else {
				failed++;
				failMessage.append(test).append('\n');
			}
		}
		
		
		
		System.out.println(Color.green(Integer.toString(passed)) + " passed and " + Color.red(Integer.toBinaryString(failed)) + " failed");
		if (failMessage.length() != 0) System.out.println(failMessage);
	}
	
	public static void test (File directory) throws IOException{
		test(directory(directory));
	}
}
