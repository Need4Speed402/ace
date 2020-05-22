package value;

import parser.Color;
import value.effect.Effect;

public class ValueEffect implements Value{
	private final Value parent;
	private final EffectNode effects;
	
	public ValueEffect(Value parent) {
		this(parent, (EffectNode) null);
	}
	
	public ValueEffect(Value parent, Effect ... effects) {
		this(parent, EffectKnown.create(null, effects));
	}
	
	public ValueEffect(Value parent, Value ... values) {
		this(parent, EffectProbe.create(null, values));
	}
	
	public ValueEffect(Value parent, Object ... effects) {
		EffectNode current = null;
		
		for (int i = effects.length - 1; i >= 0; i--) {
			Object effect = effects[i];
			
			if (effect instanceof Effect) {
				current = new EffectKnown(current, (Effect) effect);
			}else if (effect instanceof Value) {
				current = EffectProbe.create(current, (Value) effect);
			}else {
				throw new RuntimeException("illegal arguments");
			}
		}
		
		if (parent instanceof ValueEffect) {
			parent = ((ValueEffect) parent).parent;
		}else if (parent instanceof ValueProbe) {
			throw new RuntimeException("Cannot use yet to be known value as parent for effect");
			//if (current != null) current = current.removeDups((ValueProbe) parent, false);
		}
		
		this.effects = current;
		this.parent = parent;
	}
	
	private ValueEffect (Value parent, EffectNode effects) {
		if (parent instanceof ValueEffect) {
			parent = ((ValueEffect) parent).parent;
		}else if (parent instanceof ValueProbe) {
			throw new RuntimeException("Cannot use yet to be known value as parent for effect");
			//if (effects != null) effects = effects.removeDups((ValueProbe) parent, false);
		}
		
		this.parent = parent;
		this.effects = effects;
	}
	
	@Override
	public Value call(Value v) {
		Value c = this.parent.call(v);
		return new ValueEffect(c, this, c);
	}
	
	@Override
	public Value getID(Getter getter) {
		Value c = this.parent.getID(getter);
		return new ValueEffect(c, this, c);
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		Value pr = this.parent.resolve(probe, value);
		
		if (this.effects == null) {
			return new ValueEffect(pr);
		}else {
			return new ValueEffect(pr, this.effects.resolve(probe, value, this.parent, pr));
		}
	}
	
	public EffectNode getEffects () {
		return this.effects;
	}
	
	public Value getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString() + "\n");
		b.append(Color.indent(this.parent.toString(), "|-", this.effects == null ? "  " : "| "));
		
		EffectNode current = this.effects;
		
		while (current != null) {
			b.append("\n");
			b.append(Color.indent(current.toString(), "|-", current.next == null ? "  " : "| "));
			current = current.next;
		}
		
		return b.toString();
	}
	
	public static abstract class EffectNode {
		public final EffectNode next;
		private final int length;
		
		public EffectNode (EffectNode next) {
			this.next = next;
			this.length = (this.next == null ? 0 : this.next.length) + 1;
		}
		
		public abstract EffectNode removeDups (ValueProbe value, boolean found);
		
		public abstract EffectNode resolve (ValueProbe probe, Value value, Value parent, Value rParent);
		public abstract EffectNode rebind (EffectNode root);
	}
	
	public static class EffectProbe extends EffectNode {
		public final ValueProbe value;
		
		public EffectProbe (EffectNode next, ValueProbe value) {
			super(next);
			this.value = value;
		}
		
		@Override
		public EffectNode removeDups(ValueProbe probe, boolean found) {
			if (probe != this.value) {
				return new EffectProbe(this.next == null ? null : this.next.removeDups(probe, found), this.value);
			}else if (found) {
				return this.next == null ? null : this.next.removeDups(probe, found);
			}else {
				return new EffectProbe(this.next == null ? null : this.next.removeDups(probe, true), this.value);
			}
		}
		
		@Override
		public EffectNode resolve(ValueProbe probe, Value value, Value parent, Value rParent) {
			EffectNode next = this.next == null ? null : this.next.resolve(probe, value, parent, rParent);
			Value v = this.value == parent ? rParent : this.value.resolve(probe, value);
			
			return EffectProbe.create(next, v);
		}
		
		@Override
		public EffectNode rebind(EffectNode root) {
			return new EffectProbe(this.next == null ? root : this.next.rebind(root), this.value);
		}
		
		public Value getValue () {
			return this.value;
		}
		
		public static EffectNode create (EffectNode current, Value ... values) {
			for (int i = values.length - 1; i >= 0; i--) {
				if (values[i] instanceof ValueEffect) {
					EffectNode inode = ((ValueEffect) values[i]).effects;
					
					if (current == null) {
						current = inode;
					}else if (inode != null) {
						current = inode.rebind(current);
					}
				}else if (values[i] instanceof ValueProbe){
					current = new EffectProbe(current, (ValueProbe) values[i]);
				}
			}
			
			return current;
		}
		
		@Override
		public String toString() {
			return this.value.toString();
		}
	}
	
	public static class EffectKnown extends EffectNode {
		public final Effect effect;
		
		public EffectKnown (EffectNode node, Effect effect) {
			super(node);
			this.effect = effect;
		}
		
		public Effect getEffect () {
			return this.effect;
		}
		
		@Override
		public EffectNode removeDups(ValueProbe probe, boolean found) {
			return new EffectKnown(this.next == null ? null : this.next.removeDups(probe, found), this.effect);
		}
		
		@Override
		public EffectNode resolve(ValueProbe probe, Value value, Value parent, Value rParent) {
			return new EffectKnown(this.next == null ? null : this.next.resolve(probe, value, parent, rParent), this.effect.resolve(probe, value));
		}
		
		@Override
		public EffectNode rebind(EffectNode root) {
			return new EffectKnown(this.next == null ? root : this.next.rebind(root), this.effect);
		}
		
		@Override
		public String toString() {
			return effect.toString();
		}
		
		public static EffectNode create (EffectNode current, Effect ... effects) {
			for (int i = effects.length - 1; i >= 0; i--) {
				current = new EffectKnown(current, effects[i]);
			}
			
			return current;
		}
	}
}
