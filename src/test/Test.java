package test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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
	private final String expected;
	private String result;
	private long duration;
	
	public Test (String name, Node body, String expected) {
		this.name = name;
		this.body = body;
		this.expected = expected;
	}
	
	public long getDuration() {
		this.getResult();
		
		return this.duration;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getExpected() {
		return this.expected;
	}
	
	public boolean isSuccessful () {
		return this.getResult().equals(this.getExpected());
	}
	
	public String getResult () {
		if (this.result == null) {
			try {
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				Runtime r = new Runtime(new PrintStream(bytes));
				
				long start = System.nanoTime();
				ValueDefaultEnv.run(r, this.body);
				
				this.duration = System.nanoTime() - start;
				this.result = Charset.forName("UTF8").decode(ByteBuffer.wrap(bytes.toByteArray())).toString();
			}catch (Throwable e) {
				e.printStackTrace();
				this.result = e.toString();
			}
		}
		
		return this.result;
	}
	
	public static Test[] parseTest (String defName, TokenBlock scope) {
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
	
	private static class SLink {
		public final int value;
		public final SLink next;
		public final int length;
		
		public SLink (int value, SLink next) {
			this.value = value;
			this.next = next;
			this.length = (next == null ? 0 : next.length) + 1;
		}
		
		@Override
		public String toString() {
			return this.toString("");
		}
		
		public String toString(String padding) {
			StringBuilder b = new StringBuilder(this.length * 2);
			SLink current = this;
			
			int lastClass = 0;
			
			b.append(padding);
			
			while (current != null) {
				int c = current.value >> 8;
		
				if (c != lastClass) {
					b.append(Color.delimiter).append('[').append(c).append("m");
					lastClass = c;
				}
				
				b.append((char) (current.value & 0xFF));
				
				if ((current.value & 0xFF) == '\n') {
					b.append(padding);
					b.append(Color.delimiter).append('[').append(lastClass).append("m");
				}
				
				current = current.next;
			}
			
			b.append(Color.delimiter).append("[0m");
			return b.toString();
		}
	}
	
	private static SLink dif (String a, String b, int ai, int bi) {
		if (ai >= a.length() && bi >= b.length()) {
			return null;
		}else if (ai >= a.length()) {
			return new SLink(b.charAt(bi) + 0x1F00, dif(a, b, ai, bi + 1));
		}else if (bi >= b.length()) {
			return new SLink(a.charAt(ai) + 0x2000, dif(a, b, ai + 1, bi));
		}else if (a.charAt(ai) == b.charAt(bi)) {
			return new SLink(a.charAt(ai), dif(a, b, ai + 1, bi + 1));
		}else {
			SLink p1 = dif(a, b, ai + 1, bi);
			SLink p2 = dif(a, b, ai, bi + 1);
			
			if (p1.length < p2.length) {
				return new SLink(a.charAt(ai) + 0x2000, p1);
			}else {
				return new SLink(b.charAt(bi) + 0x1F00, p2);
			}
		}
	}
	
	public static void test (Test[] tests) {
		TestList failed = new TestList();
		
		long time = 0;
		
		for (Test test : tests) {
			if (!test.isSuccessful()) {
				failed.push(test);
			}else {
				System.out.println(Color.bgGreen(Color.white(" PASSED ")) + new TokenString(test.getName()).toString() + " in " + Packages.formatTime(test.getDuration()));
			}
			
			time += test.getDuration();
		}
		
		for (Test test : failed) {
			System.out.println(Color.bgRed(Color.white(" FAILED ")) + new TokenString(test.getName()).toString() + " in " + Packages.formatTime(test.getDuration()));
			System.out.println(dif(test.getExpected(), test.getResult(), 0, 0).toString(Color.bgRed(" ") + " "));
		}
		
		System.out.println(Color.green(Integer.toString(tests.length - failed.size())) + " passed and " + Color.red(Integer.toString(failed.size())) + " failed in " + Packages.formatTime(time));
	}
	
	public static void test (File directory) throws IOException {
		test(directory(directory));
	}
}
