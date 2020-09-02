package parser.token.node;

import parser.ParserException;
import parser.Stream;
import parser.token.Resolver;
import parser.token.Token;
import parser.token.resolver.Source;
import parser.token.resolver.Unsafe;
import parser.token.resolver.Virtual;
import value.node.Node;

public class TokenBuiltin implements Token{
	private final Node containing;
	
	public TokenBuiltin (Stream s) {
		this.containing = readBuiltin(s);
	}
	
	@Override
	public Node createNode() {
		return this.containing;
	}
	
	public static Node readBuiltin (Stream s) {
		StringBuilder b = new StringBuilder();
		
		while (true) {
			if (!s.hasChr()) throw new ParserException("Unexpected end of input");
			
			if (s.isNext("]")) break;
			b.append(s.chr());
		}
		
		String[] path = b.toString().trim().split(" ");
		Resolver current = Unsafe.instance;
		
		for (int i = 0; i < path.length && current != null; i++) {
			if (current instanceof Virtual) {
				current = ((Virtual) current).getResolver(path[i]);
			}else {
				current = null;
			}
		}
		
		if (current != null && current instanceof Source) {
			return ((Source) current).getSource();
		}else {
			throw new Error(b.toString() + " does not point to a valid builtin");
		}
	}
}
