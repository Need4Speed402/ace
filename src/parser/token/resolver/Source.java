package parser.token.resolver;

import java.io.File;
import java.io.FileInputStream;

import parser.Color;
import parser.Packages;
import parser.Stream;
import parser.token.Resolver;
import parser.token.Token;
import parser.token.syntax.TokenBase;
import value.Value;

public class Source extends Resolver{
	private final Value source;
	
	public Source (String name, File f) {
		super(name);
		
		this.source = Value.delegate(() -> {
			System.out.println("Loading: " + f);
			Stream s;
			long p1 = System.nanoTime();
			
			try {
				s = new Stream(new FileInputStream(f));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			try {
				Token ast = new TokenBase(s);
				long p2 = System.nanoTime();
				Packages.AST_TIME += p2 - p1;
				
				Value node = ast.createNode();
				Packages.NODE_TIME += System.nanoTime() - p2;
				
				return node;
			}catch (Exception e) {
				System.out.println(f.getAbsolutePath() + ":" + (s.getLine() + 1) + ":" + (s.getCol() + 1) + ": " + e.getMessage());
				
				throw e;
			}
		});
	}
	
	public Source(String name, Value node) {
		super(name);
		this.source = node;
	}
	
	@Override
	public Value createNode() {
		Value set = Value.id();
		Value get = Value.id();
		Value param = Value.id();
		Value root = Value.id();
		Value rootparam = Value.id();
		
		return Value.call(Unsafe.FUNCTION, root, Value.env(
			Value.call(Unsafe.MUTABLE, Value.id(), Value.call(Unsafe.FUNCTION, set, Value.env(
				Value.call(Unsafe.FUNCTION, get, Value.env(
					Value.call(Unsafe.DO, Value.call(set, Value.call(Unsafe.FUNCTION, param, Value.env(
						Value.call(Unsafe.DO,
							Value.call(Unsafe.DO,
								Value.call(set, Value.call(root, Unsafe.PARENT, Value.id(this.getName()))),
								Value.call(set, Value.call(
									Value.env(this.source),
									Value.call(Unsafe.FUNCTION, rootparam, Value.env(
										Value.call(Unsafe.SCOPE, Value.call(Unsafe.COMPARE, Value.id("root"), rootparam, Value.env(
											rootparam
										), Value.env(
											Value.call(root, rootparam)
										)))
									))
								))
							),
							Value.call(get, Value.id(), param)
						)
					))), Value.call(Unsafe.FUNCTION, param, Value.env(Value.call(get, Value.id(), param))))
				))
			)))
		));
	}

	@Override
	public String toString() {
		return Color.cyan(this.getName());
	}
}
