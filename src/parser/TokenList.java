package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import parser.token.Token;
import parser.token.syntax.TokenImmediate;

public class TokenList implements Iterable<Token> {
	private final List<Token> tokens;
	
	public TokenList () {
		this.tokens = new ArrayList<Token>();
	}
	
	public void push (Token t) {
		this.tokens.add(t);
	}
	
	public void clear () {
		this.tokens.clear();
	}
	
	public Token[] toArray () {
		Token[] tokens = new Token[this.tokens.size()];
		
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = this.tokens.get(i);
		}
		
		return tokens;
	}
	
	public TokenList sub (int i) {
		return this.sub(i, this.size());
	}
	
	public TokenList sub (int i, int end) {
		TokenList n = new TokenList();
		
		for (; i < end; i++) {
			n.push(this.get(i));
		}
		
		return n;
	}
	
	public int size () {
		return this.tokens.size();
	}
	
	public Token get (int i) {
		return this.tokens.get(i);
	}
	
	public Token first () {
		return this.tokens.get(0);
	}
	
	public Token last () {
		return this.tokens.get(this.tokens.size() - 1);
	}
	
	public void set (int i, Token t) {
		this.tokens.set(i, t);
	}
	
	public Token asImmediates () {
		Token ret = this.tokens.get(0);
		
		for (int i = 1; i < this.tokens.size(); i++) {
			ret = new TokenImmediate(ret, this.tokens.get(i));
		}
		
		return ret;
	}

	@Override
	public Iterator<Token> iterator() {
		return this.tokens.iterator();
	}
}
