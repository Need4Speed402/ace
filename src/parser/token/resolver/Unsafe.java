package parser.token.resolver;

import value.Identifier;
import value.Value;

public class Unsafe extends Virtual {
	public static final Identifier FUNCTION = Value.id();
	public static final Identifier COMPARE = Value.id();
	public static final Identifier ASSIGN = Value.id();
	public static final Identifier MUTABLE = Value.id();
	public static final Identifier CONSOLE = Value.id();
	
	public static final Identifier PARENT = Value.id();
	
	private static final Value TEMP1 = Value.id();
	private static final Value TEMP2 = Value.id();
	
	public static final Value IDENTITY = Value.call(FUNCTION, TEMP1, Value.env(TEMP1));
	public static final Value SCOPE = Value.call(FUNCTION, TEMP2, Value.env(Value.call(TEMP2, IDENTITY)));
	public static final Value DO = Value.call(FUNCTION, Value.id(), Value.env(IDENTITY));
	
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
