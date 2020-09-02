package value;

import java.util.Map.Entry;

import parser.Color;
import parser.ProbeSet;
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
	
	private static Value create (Value parent, EffectNode tail) {
		if (tail != null) {
			ResolverMutable mut = new ResolverMutable();
			
			tail = tail.resolve(mut);
			parent = parent.resolve(mut);
			
			ProbeSet set = new ProbeSet(parent);
			
			Probe[] deps = new Probe[mut.getMap().size()];
			{
				int i = 0;
				
				for (Probe p : mut.getMap().keySet()) {
					deps[i++] = p;
				}
			}
			
			for (Value p : mut.getMap().values()) {
				p.getResolves(set);
			}
			
			EffectNode declares = null;
			
			for (Entry<Probe, Value> entry : mut.getMap().entrySet()) {
				// if the mutation has effects outside of the current scope,
				// that means we cannot eliminate the mutation at this point
				// and we must store the mutation declare back on the effects
				if (set.has(entry.getKey())) {
					declares = new EffectNode(new EffectDeclare(entry.getKey(), entry.getValue()), declares);
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
	
	@Override
	public Value call(Value v) {
		return ValueEffect.create(this.parent.call(v), this.tail);
	}
	
	@Override
	public Value getID(Getter getter) {
		return ValueEffect.create(this.parent.getID(getter), this.tail);
	}
	
	@Override
	public Value resolve(Resolver res) {
		EffectNode tail = this.tail == null ? null : this.tail.resolve(res);
		Value parent = this.parent.resolve(res);
		
		if (parent == this.parent & tail == this.tail) {
			return this;
		}else{
			return ValueEffect.create(parent, tail);
		}
	}
	
	@Override
	public void getResolves(ProbeSet set) {
		this.parent.getResolves(set);
		
		EffectNode current = this.tail;
		
		while (current != null) {
			current.effect.getResolves(set);
			current = current.prev;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString()).append('\n');
		b.append(Color.indent(this.parent.toString(), "|-", "| ")).append('\n');
		
		if (this.tail != null) this.tail.print(b, true);

		return b.toString();
	}
	
	private static class EffectNode implements ProbeSet.ProbeContainer{
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
		
		public void print (StringBuilder b, boolean first) {
			if (this.prev != null) this.prev.print(b, false);
			
			b.append(Color.indent(this.effect.toString(), "|-", first ? "  " : "| "));
			if (!first) b.append('\n');
		}
		
		@Override
		public void getResolves(ProbeSet set) {
			if (this.prev != null) this.prev.getResolves(set);
			this.effect.getResolves(set);
		}
	}
}
