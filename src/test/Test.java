package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import parser.Color;
import parser.Packages;
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
	
	private final byte[] expected;
	private byte[] input;
	private byte[] output;
	
	private long duration;
	
	public Test (String name, Node body, byte[] expected, byte[] input) {
		this.name = name;
		this.body = body;
		this.expected = expected;
		this.input = input;
	}
	
	public long getDuration() {
		this.getResult();
		
		return this.duration;
	}
	
	public String getName() {
		return this.name;
	}
	
	public byte[] getExpected() {
		return this.expected;
	}
	
	public boolean isSuccessful () {
		byte[] res = this.getResult();
		byte[] ex = this.getExpected();
		
		if (res.length != ex.length) return false;
		
		for (int i = 0; i < res.length; i++) {
			if (res[i] != ex[i]) return false;
		}
		
		return true;
	}
	
	public byte[] getResult () {
		if (this.output == null) {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			Runtime r = new Runtime(bytes, new ByteArrayInputStream(this.input));
			
			long start = System.nanoTime();
			
			try {
				ValueDefaultEnv.run(r, this.body);
				
				this.duration = System.nanoTime() - start;
			}catch (Throwable e) {
				this.duration = System.nanoTime() - start;
				this.output = new byte[0];
				
				e.printStackTrace();
			}finally {
				this.output = bytes.toByteArray();
			}
		}
		
		return this.output;
	}
	
	public static Test[] parseTest (String defName, TokenBlock scope) {
		Token[] tokens = scope.getElements();
		
		TokenScope testCase = null;
		String testName = null;
		
		byte[] expect = null;
		byte[] input = null;
		
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
				if (expect != null) throw new ParserException("there can only be one expected output defined per test");
				
				expect = ((TokenIdentifier) body).getIdentifier().getBytes();
			}else if (name.equals("name")) {
				if (testName != null) throw new ParserException("A name has already been defined for the test: " + testName);
				
				testName = ((TokenIdentifier) body).getIdentifier();
			}else if (name.equals("in")) {
				if (input != null) throw new ParserException("there can only be one input defined per test");
				
				input = ((TokenIdentifier) body).getIdentifier().getBytes();
			} else {
				throw new ParserException("Unknown directive: " + name);
			}
		}
		
		if (testCase != null || expect != null) {
			if (testCase == null || expect == null) {
				throw new ParserException("Test must specify both a test case and the result of the execution");
			}
			
			if (input == null) {
				input = new byte[0];
			}
			
			list.push(new Test(testName == null ? defName : testName, testCase.createNode(), expect, input));
		}
		
		return list.toArray();
	}
	
	public static Test[] directory (File file) throws IOException{
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			
			TestList list = new TestList();
			
			for (File f : files) {
				list.push(directory(f));
			}
			
			return list.toArray();
		}else {
			return Test.parseTest(file.getName(), new TokenBase(new Stream(new FileInputStream(file))));
		}
	}
	
	public static void test (Test[] tests) throws IOException{
		TestList failed = new TestList();
		
		long time = 0;
		
		for (Test test : tests) {
			if (!test.isSuccessful()) {
				failed.push(test);
			}else {
				System.out.println(Color.bgGreen(Color.white(" PASS ")) + new TokenString(test.getName()).toString() + " in " + Packages.formatTime(test.getDuration()));
			}
			
			time += test.getDuration();
		}
		
		for (Test test : failed) {
			System.out.println(Color.bgRed(Color.white(" FAIL ")) + new TokenString(test.getName()).toString() + " in " + Packages.formatTime(test.getDuration()));
			//System.out.println(dif(test.getExpected(), test.getResult(), 0, 0).toString(Color.bgRed(" ") + " "));
			System.out.write(test.getResult());
			System.out.println();
		}
		
		System.out.println(Color.green(Integer.toString(tests.length - failed.size())) + " passed and " + Color.red(Integer.toString(failed.size())) + " failed in " + Packages.formatTime(time));
	}
	
	public static void test (File directory) throws IOException {
		test(directory(directory));
	}
}
