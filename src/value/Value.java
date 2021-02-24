package value;

import runtime.Effect;
import runtime.EffectList;
import value.resolver.Resolver;

public interface Value {
	public static final int DEFAULT_ID = 0;
	
	public CallReturn call (Value v);
	
	public default Value resolve (Resolver resolver) {
		return this;
	}
	
	public default int getID () {
		return DEFAULT_ID;
	}
	
	public static int add (int ... nums) {
		int add = 0;
		for (int i : nums) add += i;
		if (add != 0) add += nums.length - 1;
		return add;
	}
	
	public static String print (String name, Object ... objects) {
		StringBuilder b = new StringBuilder ();
		b.append(name).append('\n');
		
		for (int ii = 0; ii < objects.length; ii++) {
			String s = objects[ii].toString();
			b.append(ii == objects.length - 1 ? '\u2514' : '\u251C');
			
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				b.append(c);
				if (c == '\n') {
					b.append(ii == objects.length - 1 ? " " : "\u2502");
				}
			}
			
			if (ii < objects.length - 1) b.append('\n');
		}
		
		return b.toString();
	}
	
	public class CallReturn {
		public final Value value;
		public final Effect effect;
		
		public CallReturn (Value value) {
			this.value = value;
			this.effect = Effect.NO_EFFECT;
		}
		
		public CallReturn (Value value, Effect effect) {
			this.value = value;
			this.effect = effect;
		}
		
		public CallReturn (Value value, Effect ... effects) {
			this.value = value;
			this.effect = EffectList.create(effects);
		}
		
		public CallReturn call (Value value) {
			CallReturn res = this.value.call(value);
			return new CallReturn(res.value, this.effect, res.effect);
		}
		
		public CallReturn call (CallReturn value) {
			CallReturn res = this.value.call(value.value);
			return new CallReturn(res.value, this.effect, value.effect, res.effect);
		}
		
		public CallReturn resolve (Resolver resolver) {
			return new CallReturn(this.value.resolve(resolver), this.effect.resolve(resolver));
		}
		
		@Override
		public String toString() {
			return Value.print("CallReturn", this.value, this.effect);
		}
	}
}
