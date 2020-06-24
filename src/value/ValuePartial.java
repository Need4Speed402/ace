package value;

import parser.Color;
import value.effect.Runtime;
import value.effect.Runtime.Resolve;

public abstract class ValuePartial implements Value {
	@Override
	public abstract Value run(Runtime r);
	
	@Override
	public Value call (Value arg) {
		return new Call(this, arg);
	}
	
	@Override
	public Value getID (Getter getter) {
		return new Identifier(this, getter);
	}
	
	public static class Probe extends ValuePartial {
		@Override
		public Value resolve(Resolver r) {
			if (r instanceof ProbeResolver) {
				ProbeResolver pr = (ProbeResolver) r;
				
				if (pr.probe == this) {
					return pr.use();
				}
			}
			
			return this;
		}
		
		@Override
		public Value run(Runtime r) {
			Resolve res = r.memory;
			
			while (res != null) {
				if (res.probe == this) {
					return res.value;
				}
				
				res = res.next;
			}
			
			throw new Error("neither compile time or runtime info is able to satisfy this probe. This indicates a bug with the interpreter. Unknown: " + this);
		}
	}
	
	public static class Call extends ValuePartial {
		public final Value parent;
		public final Value argument;
		
		public Call (Value parent, Value argument) {
			this.parent = parent;
			this.argument = argument;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString()).append('\n');
			b.append(Color.indent(this.parent.toString(), "|-", "| ")).append('\n');
			b.append(Color.indent(this.argument.toString(), "|-", "  "));
			
			return b.toString();
		}
		
		@Override
		public Value run(Runtime r) {
			Value a = this.parent.run(r);
			Value b = this.argument.run(r);
			
			return a.call(b).run(r);
		}
		
		@Override
		public Value resolve(Resolver res) {
			return this.parent.resolve(res).call(this.argument.resolve(res));
		}
	}
	
	public static class Identifier extends ValuePartial {
		public final Value parent;
		public final Getter getter;
		
		public Identifier (Value parent, Getter getter) {
			this.parent = parent;
			this.getter = getter;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString()).append('\n');
			b.append(Color.indent(this.parent.toString(), "|-", "| ")).append('\n');
			b.append(Color.indent(this.getter.toString(), "|-", "  "));
			
			return b.toString();
		}
		
		@Override
		public Value run(Runtime r) {
			Value p = this.parent.run(r);
			
			return p.getID(this.getter).run(r);
		}
		
		@Override
		public Value resolve(Resolver res) {
			return this.parent.resolve(res).getID(this.getter.resolve(res));
		}
	}
}