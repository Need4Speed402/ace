package value.intrinsic;

import parser.Color;
import value.Value;
import value.ValueFunction;
import value.node.NodeIdentifier;
import value.resolver.Resolver;

public class Function implements Value{
	public static final Value instance = new Function();
	
	@Override
	public Value call(Value identv) {
		return identv.getID((parent, ident) ->
			new ValueFunction(body -> new ValueFunction(arg -> body.call(new Env(arg, ident))))
		);
	}
	
	@Override
	public String toString() {
		return "Function()";
	}
	
	private static class Env implements Value {
		private final Value arg;
		private final int value;
		
		public Env (Value arg, int value) {
			this.arg = arg;
			this.value = value;
		}
		
		@Override
		public Value call(Value v) {
			return v.getID(new Arg(this.arg, this.value));
		}
		
		@Override
		public Value resolve(Resolver res) {
			Value v = this.arg.resolve(res);
			
			if (v == this.arg) {
				return this;
			}else {
				return new Env(v, this.value);
			}
		}
		
		@Override
		public int complexity() {
			return this.arg.complexity() + 1;
		}
		
		@Override
		public String toString() {
			return "FunctionDefinition(" + NodeIdentifier.asString(this.value) + ", " + this.arg + ")";
		}
	}
	
	private static class Arg implements Getter {
		private final Value arg;
		private final int value;
		
		private Arg (Value arg, int value) {
			this.arg = arg;
			this.value = value;
		}
		
		@Override
		public Value resolved(Value parent, int value) {
			if (this.value == value) {
				return this.arg;
			}else {
				return parent;
			}
		}
		
		@Override
		public Getter resolve(Resolver res) {
			Value a = this.arg.resolve(res);
			
			if (a == this.arg) {
				return this;
			}else {
				return new Arg(a, this.value);
			}
		}
		
		@Override
		public int complexity() {
			return 1 + this.arg.complexity();
		}
		
		@Override
		public String toString() {
			return "FunctionParameter(" + NodeIdentifier.asString(this.value) + " ? " + this.arg + ")";
		}
		
		@Override
		public String toString(Value ident) {
			StringBuilder b = new StringBuilder();
			b.append("FunctionParameter\n");
			b.append(Color.indent(NodeIdentifier.asString(this.value) + " = " + ident + " ? " + this.arg, "|-", "| ")).append('\n');
			b.append(Color.indent(ident.toString(), "|-", "  "));
			return b.toString();
		}
	}
}
