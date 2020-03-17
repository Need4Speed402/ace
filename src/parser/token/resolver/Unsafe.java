package parser.token.resolver;

import value.node.Node;
import value.node.NodeIdentifier;

public class Unsafe extends Virtual {
	public static final NodeIdentifier FUNCTION = Node.id();
	public static final NodeIdentifier COMPARE = Node.id();
	public static final NodeIdentifier ASSIGN = Node.id();
	public static final NodeIdentifier MUTABLE = Node.id();
	public static final NodeIdentifier CONSOLE = Node.id();
	
	public static final NodeIdentifier PARENT = Node.id();
	
	private static final Node TEMP1 = Node.id();
	private static final Node TEMP2 = Node.id();
	
	public static final Node IDENTITY = Node.call(FUNCTION, TEMP1, Node.env(TEMP1));
	public static final Node SCOPE = Node.call(FUNCTION, TEMP2, Node.env(Node.call(TEMP2, IDENTITY)));
	public static final Node DO = Node.call(FUNCTION, Node.id(), Node.env(IDENTITY));
	
	public Unsafe() {
		super("unsafe",
			new Source("compare", COMPARE),
			new Source("assign", ASSIGN),
			new Source("Mutable", MUTABLE),
			new Source("console", CONSOLE),
			
			new Virtual("root",
				new Source("``", DO),
				new Source("Procedure", IDENTITY),
				new Source("Environment", IDENTITY),
				new Source("Package", SCOPE),
				new Source("Scope", SCOPE),
				new Source("Function", FUNCTION)
			)
		);
	}
	
}
