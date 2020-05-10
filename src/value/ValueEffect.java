package value;

import value.effect.Effect;

public class ValueEffect implements Value{
	private final Value parent;
	private final EffectNode effects;
	
	public ValueEffect(Value parent) {
		this(parent, (EffectNode) null);
	}
	
	public ValueEffect(Value parent, Effect ... effects) {
		this(parent, EffectNodeList.create(null, effects));
	}
	
	public ValueEffect(Value parent, Value ... values) {
		this(parent, EffectNodeValue.create(null, values));
	}
	
	public ValueEffect(Value parent, Value value, Effect effect) {
		this(parent, EffectNodeValue.create(new EffectNodeList(null, effect), value));
	}
	
	private ValueEffect (Value parent, EffectNode effects) {
		if (parent instanceof ValueEffect) throw new RuntimeException("ValueEffect cannot inherit from another ValueEffect");
		
		this.parent = parent;
		this.effects = effects;
	}
	
	@Override
	public Value call(Value v) {
		Value c = this.parent.call(clear(v));
		//System.out.println(this);
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
	
	public EffectNode getEffects () {
		return this.effects;
	}
	
	@Override
	public boolean canCreateEffects() {
		return this.effects != null;
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
		if (v.canCreateEffects()) {
			return new ValueEffect(clear(v2), v, v2);
		}else {
			return v2;
		}
	}
	
	public static Value clear (Value v) {
		if (v instanceof ValueEffect) {
			v = ((ValueEffect) v).parent;
		}
		
		if (v instanceof Clear) {
			return v;
		}else if (v instanceof ValueProbe) {
			return new Clear((ValueProbe) v);
		}else {
			return v;
		}
	}
	
	private static class Clear extends ValueProbe {
		private final ValueProbe parent;
		
		public Clear (ValueProbe parent) {
			this.parent = parent;
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			return clear(this.parent.resolve(probe, value));
		}
		
		@Override
		public boolean canCreateEffects() {
			return false;
		}
		
		@Override
		public String toString() {
			return "value.ValueEffect.Clear " + this.parent.toString();
		}
	}
	
	public static abstract class EffectNode {
		public final EffectNode next;
		private final int length;
		
		public EffectNode (EffectNode next) {
			this.next = next;
			this.length = (this.next == null ? 0 : this.next.length) + 1;
		}
		
		public abstract EffectNode resolve (ValueProbe probe, Value value);
		public abstract EffectNode rebind (EffectNode root);
	}
	
	public static class EffectNodeValue extends EffectNode {
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
				}else if (values[i].canCreateEffects()){
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
		public final Effect effect;
		
		public EffectNodeList (EffectNode node, Effect effect) {
			super(node);
			this.effect = effect;
		}
		
		public Effect getEffect () {
			return this.effect;
		}
		
		@Override
		public EffectNode resolve(ValueProbe probe, Value value) {
			return new EffectNodeList(this.next == null ? null : this.next.resolve(probe, value), this.effect);
		}
		
		@Override
		public EffectNode rebind(EffectNode root) {
			return new EffectNodeList(this.next == null ? root : this.next.rebind(root), this.effect);
		}
		
		@Override
		public String toString() {
			return effect.toString();
		}
		
		public static EffectNode create (EffectNode current, Effect ... effects) {
			for (int i = effects.length - 1; i >= 0; i--) {
				current = new EffectNodeList(current, effects[i]);
			}
			
			return current;
		}
	}
}
