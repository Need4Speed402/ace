package value;

import runtime.Effect;
import runtime.EffectList;
import value.resolver.Resolver;
import value.resolver.Resolver.Resolvable;

public interface Value extends Resolvable{
	public static final int DEFAULT_ID = 0;
	public static final char[] numberChars = "0123456789ABCDEF".toCharArray();
	
	public CallReturn call (Value v);
	
	@Override
	public default CallReturn resolve(Resolver resolver) {
		return new CallReturn(this, (Effect) null);
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
	
	public static String printHash (String name, int hash, Object ... objects) {
		String fullName = name + " " +	
			+ numberChars[(hash >> 28) & 0xF]
			+ numberChars[(hash >> 24) & 0xF]
			+ numberChars[(hash >> 20) & 0xF]
			+ numberChars[(hash >> 16) & 0xF]
			+ numberChars[(hash >> 12) & 0xF]
			+ numberChars[(hash >> 8) & 0xF]
			+ numberChars[(hash >> 4) & 0xF]
			+ numberChars[(hash >> 0) & 0xF];
		
		if (objects.length == 0) {
			return fullName;
		}else {
			return Value.print(fullName, objects);
		}
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
			//we want to execute the resolve for the effects first because effects are run before the values are calculated
			//this wouldn't matter if our system was completely immutable, but it isn't.
			Effect effect = resolver.resolveEffect(this.effect);
			Value value = resolver.resolveValue(this.value);

			return new CallReturn(value, effect);
		}
		
		@Override
		public String toString() {
			return Value.print("CallReturn", this.value, this.effect);
			//return this.value.toString();
		}
		
		@Override
		public int hashCode() {
			return (this.value.hashCode() + this.effect.hashCode()) * 3;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CallReturn) {
				CallReturn cr = (CallReturn) obj;
				return this.value.equals(cr.value) && this.effect.equals(cr.effect);
			}
			return false;
		}
	}
}
