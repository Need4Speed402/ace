package value;

import java.util.Map.Entry;

import runtime.Effect;
import value.ValuePartial.Probe;
import value.intrinsic.Mutable.EffectDeclare;
import value.resolver.Resolver;
import value.resolver.ResolverMutable;

public class ValueEffect implements Value{
	private final Value parent;
	private final EffectNode tail;
	
	private ValueEffect (Value parent, EffectNode tail) {
		this.parent = parent;
		this.tail = tail;
	}
	
	public static Value create (Value parent, Effect ... effects) {
		EffectNode current = null;
		
		for (int i = 0; i < effects.length; i++){
			current = new EffectNode(effects[i], current);
		}
		
		return ValueEffect.create(parent, current);
	}
	
	public static Value create (Value parent, EffectNode tail) {
		if (tail != null) {
			ResolverMutable mut = new ResolverMutable();
			
			tail = tail.resolve(mut);
			EffectDeclare[] entries = new EffectDeclare[mut.getMap().size()];
			
			if (entries.length > 0) {
				{
					int i = 0;
					
					for (Entry<Probe, Value> entry : mut.getMap().entrySet()) {
						entries[i++] = new EffectDeclare(entry.getKey(), entry.getValue());
					}
				}
				
				parent = parent.resolve(mut);
				
				EffectNode declares = null;
				
				while (true) {
					boolean resolved = false;
					
					for (int i = 0; i < entries.length; i++) {
						EffectDeclare decl = entries[i];
						if (decl == null) continue;
						
						entries[i] = null;
						declares = new EffectNode(decl, declares);
						resolved = true;
					}
					
					if (!resolved) {
						break;
					}
				}
				
				if (declares != null) {
					if (tail != null) {
						tail = tail.concat(declares);
					}else {
						tail = declares;
					}
				}
			}
		}
		
		if (tail == null) {
			return parent;
		}else if (parent instanceof ValueEffect) {
			EffectNode peffects = ((ValueEffect) parent).tail;
			
			if (peffects != null) {
				tail = peffects.concat(tail);
			}
			
			parent = ((ValueEffect) parent).parent;
		}
		
		return new ValueEffect(parent, tail);
	}
	
	public Value getParent() {
		return this.parent;
	}
	
	public Effect[] getEffects () {
		int len = 0;
		
		EffectNode current = this.tail;
		while (current != null){
			len++;
			current = current.prev;
		}
		
		Effect[] effects = new Effect[len];
		
		current = this.tail;
		while (current != null) {
			effects[--len] = current.effect;
			current = current.prev;
		}
		
		return effects;
	}
	
	public EffectNode getEffectNode () {
		return this.tail;
	}
	
	@Override
	public Value call(Value v) {
		return ValueEffect.create(this.parent.call(v), this.tail);
	}
	
	@Override
	public int getID() {
		return this.parent.getID();
	}
	
	@Override
	public Value resolve(Resolver res) {
		return ValueEffect.create(res.cache(this.parent), this.tail.resolve(res));
	}
	
	@Override
	public String toString() {
		int len = 1;
		{
			EffectNode current = this.tail;
			
			while (current != null) {
				len ++;
				current = current.prev;
			}
		}
		
		Object[] values = new Object[len];
		values[0] = this.parent;
		
		{
			EffectNode current = this.tail;
			for (int i = values.length - 1; i >= 1; i--) {
				values[i] = current.effect;
				current = current.prev;
			}
		}
		
		return Value.print("Effects", values); 
	}
	
	private static class EffectNode {
		public final EffectNode prev;
		public final Effect effect;
		
		public EffectNode(Effect effect, EffectNode prev) {
			this.effect = effect;
			this.prev = prev;
		}
		
		public EffectNode concat (EffectNode front) {
			if (this.prev != null) {
				return new EffectNode(this.effect, this.prev.concat(front));
			}else {
				return new EffectNode(this.effect, front);
			}
		}
		
		public EffectNode resolve (Resolver res) {
			EffectNode prev = this.prev == null ? null : this.prev.resolve(res);
			Effect effect = this.effect.resolve(res);
			
			if (effect == this.effect & prev == this.prev) {
				return this;
			}else if (effect == Effect.NO_EFFECT) {
				return prev;
			}else{
				return new EffectNode(effect, prev);
			}
		}
	}
}
