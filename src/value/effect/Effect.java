package value.effect;

import value.Value;
import value.ValueEffect;

public interface Effect {
	public Value run (Runtime runtime, Value root);
	
	public static Effect[] join (Effect[] ... arrays) {
		int len = 0;
		
		for (int i = 0; i < arrays.length; i++) {
			len += arrays[i].length;
		}
		
		Effect[] effects = new Effect[len];
		
		int offset = 0;
		for (int i = 0; i < arrays.length; i++) {
			int clen = arrays[i].length;
			
			if (clen > 0) {
				System.arraycopy(arrays[i], 0, effects, offset, clen);
				offset += clen;
			}
		}
		
		return effects;
	}
	
	public static Value runAll (Runtime runtime, Value root) {
		Effect[] effects = root.getEffects();
		if (root instanceof ValueEffect) {
			root = ((ValueEffect) root).getParent();
		}
		
		for (int i = 0; i < effects.length; i++) {
			root = effects[i].run(runtime, root);
		}
		
		return root;
	}
}
