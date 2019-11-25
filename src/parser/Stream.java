package parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Stream {
	public static final char[] whitespace = " \r\n\t".toCharArray();
	public static final String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static final Charset charset = Charset.forName("UTF-8");
	
	private final String code;
	private int pointer = 0;
	
	private int line = 0, col = 0;
	
	public Stream (ByteBuffer buffer) {
		this(charset.decode(buffer).toString());
	}
	
	public Stream (byte[] code) {
		this(ByteBuffer.wrap(code));
	}
	
	public Stream (InputStream input) {
		String code;
		
		try {
			ByteBuffer buf = ByteBuffer.allocate(1024);
			int read = 0;
			
			while ((read = input.read(buf.array(), buf.position(), buf.remaining())) >= 0) {
				buf.position(buf.position() + read);
				
				if (buf.remaining() == 0) {
					ByteBuffer n = ByteBuffer.allocate(buf.capacity() * 2);
					buf.flip();
					n.put(buf);
					buf = n;
				}
			}
			
			buf.flip();
			code = charset.decode(buf).toString();
		}catch (IOException e) {
			code = "";
		}
		
		this.code = code;
	}
	
	public Stream (String code, int pointer, int line, int col) {
		this.code = code;
		this.pointer = pointer;
		this.line = line;
		this.col = col;
	}
	
	public Stream (String code) {
		this(code, 0, 0, 0);
	}
	
	public char peek () {
		return this.code.charAt(this.pointer);
	}
	
	public boolean next (String ... next) {
		for (int i = 0; i < next.length; i++) {
			int ii = this.pointer;
			
			while (ii < this.code.length()) {
				if (this.code.charAt(ii) != next[i].charAt(ii - this.pointer)) break;
				
				if (++ii - this.pointer == next[i].length()) {
					while (this.pointer < ii) this.chr();
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean next (char ... next) {
		if (!this.hasChr()) return false;
		
		char peek = this.peek();
		
		for(int i = 0; i < next.length; i++) {
			if (peek == next[i]) {
				this.chr();
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isNext (String ... next) {
		main:for (int i = 0; i < next.length; i++) {
			String str = next[i];
			
			if (str.length() + this.pointer <= this.code.length()) {
				for (int ii = 0; ii < str.length(); ii++) {
					if (this.code.charAt(ii + this.pointer) != str.charAt(ii)) {
						continue main;
					}
				}
			
				return true;
			}
		}
	
		return false;
	}
	
	public boolean isNext (char ... next) {
		if (!this.hasChr()) return false;
		
		char peek = this.peek();
		
		for(int i = 0; i < next.length; i++) {
			if (peek == next[i]) {
				return true;
			}
		}
		
		return false;
	}
	
	public char chr () {
		if (this.pointer == this.code.length()) {
			throw new RuntimeException("no more bytes in stream");
		}
		
		char c = this.code.charAt(this.pointer++);
		
		if (c == '\n') {
			this.line++;
			this.col = 0;
		}else if (c != '\r'){
			this.col++;
		}
		
		return c;
	}
	
	public boolean hasChr () {
		return this.pointer < this.code.length();
	}
	
	public int getLine() {
		return line;
	}
	
	public int getCol() {
		return col;
	}
	
	public Stream clone () {
		return new Stream(this.code, this.pointer, this.line, this.col);
	}
	
	public void set (Stream s) {
		this.pointer = s.pointer;
		this.line = s.line;
		this.col = s.col;
	}
}
