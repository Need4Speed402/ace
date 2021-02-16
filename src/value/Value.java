package value;

import value.resolver.Resolver;

public interface Value {
	public static final int DEFAULT_ID = 0;
	
	public Value call (Value v);
	
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
	
	/*public class CallReturn {
		private final Value value;
		private final Effect[] effects;
		
		public CallReturn (Value value, Effect[] effects) {
			this.value = value;
			this.effects = effects;
		}
		
		public CallReturn (Value value) {
			this.value = value;
			this.effects = new Effect[0];
		}
		
		public Value getValue() {
			return this.value;
		}
		
		public Effect[] getEffects () {
			return this.effects;
		}
		
		public Effect[] mergeEffects (CallReturn other) {
			Effect[] otherEffects = other.getEffects();
			Effect[] out = new Effect[this.effects.length + otherEffects.length];

			System.arraycopy(this.effects, 0, out, 0, this.effects.length);
			System.arraycopy(otherEffects, 0, out, this.effects.length, otherEffects.length);

			return out;
		}
	}*/
}
