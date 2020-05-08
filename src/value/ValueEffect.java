package value;

import java.util.Arrays;

import value.effect.Effect;

public class ValueEffect implements Value{
	private final Value parent;
	private final EffectNode effects;
	
	public ValueEffect(Value parent) {
		this(parent, (EffectNode) null);
	}
	
	public ValueEffect(Value parent, Effect ... effects) {
		this(parent, new EffectNodeList(null, effects));
	}
	
	public ValueEffect(Value parent, Value ... values) {
		this(parent, EffectNodeValue.create(null, values));
	}
	
	public ValueEffect(Value parent, Value value, Effect effect) {
		this(parent, EffectNodeValue.create(new EffectNodeList(null, new Effect[] {effect}), value));
	}
	
	private ValueEffect (Value parent, EffectNode effects) {
		if (parent instanceof ValueEffect) throw new RuntimeException("ValueEffect cannot inherit from another ValueEffect");
		
		this.parent = parent;
		this.effects = effects;
	}
	
	@Override
	public Value call(Value v) {
		Value c = this.parent.call(clear(v));
		
		return new ValueEffect(clear(c), this, v, c);
	}
	
	@Override
	public Value getID(Getter getter) {
		Value c = this.parent.getID(getter);
		
		return new ValueEffect(clear(c), this, c);
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		Value pr = clear(this.parent.resolve(probe, value));
		
		if (this.effects == null) {
			return new ValueEffect(pr);
		}else {
			return new ValueEffect(pr, this.effects.resolve(probe, value));
		}
	}
	
	@Override
	public Effect[] getEffects() {
		if (this.effects == null) return Effect.NO_EFFECTS;
		Effect[][] effects = new Effect[this.effects.length][];
		EffectNode current = this.effects;
		
		for (int i = 0; i < this.effects.length; i++) {
			effects[i] = current.getEffects();
			current = current.next;
		}
		
		return Effect.join(effects);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString() + "\n|-");
		ValueProbe.append(this.parent.toString(), this.effects == null ? "  " : "| ", b);
		
		EffectNode current = this.effects;
		
		while (current != null) {
			b.append("\n|-");
			ValueProbe.append(current.toString(), current.next == null ? "  " : "| ", b);
			current = current.next;
		}
		
		return b.toString();
	}
	
	public static Value wrap (Value v, Value v2) {
		if (v instanceof ValueEffect || v instanceof ValueProbe) {
			return new ValueEffect(clear(v2), v, v2);
		}else {
			return v2;
		}
	}
	
	public static Value clear (Value v) {
		if (v instanceof ValueEffect) {
			v = ((ValueEffect) v).parent;
		}
		
		if (v instanceof ValueProbe) {
			Value parent = v;
			
			return new ValueProbe () {
				@Override
				public Value resolve(ValueProbe probe, Value value) {
					return clear(parent.resolve(probe, value));
				}
			};
		}else {
			return v;
		}
	}
	
	private static abstract class EffectNode {
		public final EffectNode next;
		private final int length;
		
		public EffectNode (EffectNode next) {
			this.next = next;
			this.length = (this.next == null ? 0 : this.next.length) + 1;
		}
		
		public abstract Effect[] getEffects();
		public abstract EffectNode resolve (ValueProbe probe, Value value);
		public abstract EffectNode rebind (EffectNode root);
	}
	
	private static class EffectNodeValue extends EffectNode {
		public final Value value;
		
		public EffectNodeValue (EffectNode next, Value value) {
			super(next);
			this.value = value;
		}
		
		@Override
		public EffectNode resolve(ValueProbe probe, Value value) {
			return EffectNodeValue.create(this.next == null ? null : this.next.resolve(probe, value), this.value.resolve(probe, value));
		}
		
		@Override
		public EffectNode rebind(EffectNode root) {
			return new EffectNodeValue(this.next == null ? root : this.next.rebind(root), this.value);
		}
		
		@Override
		public Effect[] getEffects() {
			return this.value.getEffects();
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
				}else {
					current = new EffectNodeValue(current, values[i]);
				}
			}
			
			return current;
		}
		
		@Override
		public String toString() {
			return this.value.toString();
		}
	}
	
	public static class EffectNodeList extends EffectNode {
		public final Effect[] list;
		
		public EffectNodeList (EffectNode node, Effect[] list) {
			super(node);
			this.list = list;
		}
		
		@Override
		public EffectNode resolve(ValueProbe probe, Value value) {
			return new EffectNodeList(this.next == null ? null : this.next.resolve(probe, value), this.list);
		}
		
		@Override
		public EffectNode rebind(EffectNode root) {
			return new EffectNodeList(this.next == null ? root : this.next.rebind(root), this.list);
		}
		
		@Override
		public Effect[] getEffects() {
			return this.list;
		}
		
		@Override
		public String toString() {
			return Arrays.toString(this.list);
		}
	}
}
