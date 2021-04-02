package runtime;

import value.Value;
import value.Value.CallReturn;
import value.resolver.Resolver;

public class EffectList implements Effect{
	private final Effect[] effects;
	
	public static Effect create (Effect ... effects) {
		int length = 0;
		boolean passthrough = true;

		for (Effect effect : effects) {
			if (effect == Effect.NO_EFFECT) {
				passthrough = false;
				continue;
			}else if (effect instanceof EffectList) {
				passthrough = false;
				length += ((EffectList) effect).effects.length;
			}else {
				length ++;
			}
		}
		
		if (length == 0) {
			return Effect.NO_EFFECT;
		}else if (length == 1){
			for (Effect effect : effects) {
				if (effect == Effect.NO_EFFECT) continue;
				if (effect instanceof EffectList) {
					Effect[] list = ((EffectList) effect).effects;
					return list[0];
				}else {
					return effect;
				}
			}
			
			//this case will never happen
			return null;
		}else if (passthrough) {
			return new EffectList(effects);
		}else {
			Effect[] cleaned = new Effect[length];
			int current = 0;
			
			for (Effect effect : effects) {
				if (effect == Effect.NO_EFFECT) continue;
				if (effect instanceof EffectList) {
					Effect[] list = ((EffectList) effect).effects;
					System.arraycopy(list, 0, cleaned, current, list.length);
					current += list.length;
				}else {
					cleaned[current++] = effect;
				}
			}
			
			return new EffectList(cleaned);
		}
	}
	
	private EffectList (Effect[] effects) {
		this.effects = effects;
	}

	@Override
	public void run(Runtime runtime) {
		for (Effect effect : effects) {
			effect.run(runtime);
		}
	}
	
	@Override
	public CallReturn resolve(Resolver resolver) {
		Effect[] n = new Effect[this.effects.length];
		
		for (int i = 0; i < n.length; i++) {
			n[i] = resolver.resolveEffect(this.effects[i]);
		}
		
		return new CallReturn (null, EffectList.create(n));
	}
	
	@Override
	public String toString() {
		return Value.print("EffectList", (Object[]) this.effects);
	}
}
