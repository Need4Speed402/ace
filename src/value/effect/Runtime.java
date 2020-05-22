package value.effect;

import java.io.PrintStream;

import value.Value;
import value.ValueEffect;
import value.ValueProbe;

public class Runtime {
	public final PrintStream out;
	public Resolve[] memory = new Resolve[0];
	
	public Runtime(PrintStream stream) {
		this.out = stream;
	}
	
	public Runtime () {
		this.out = System.out;
	}
	
	public void setResolve (ValueProbe probe, Value value) {
		for (int i = 0; i < this.memory.length; i++) {
			if (this.memory[i].probe == probe) {
				this.memory[i] = this.memory[i].useValue(value);
				return;
			}
		}
		
		Resolve[] n = new Resolve[this.memory.length + 1];
		System.arraycopy(this.memory, 0, n, 0, this.memory.length);
		n[n.length - 1] = new Resolve(probe, value);
		this.memory = n;
	}
	
	public void run (Value root) {
		if (root instanceof ValueEffect) {
			Effect[] effects = ((ValueEffect) root).getEffects();
			
			for (int i = 0; i < effects.length; i++) {
				effects[i].run(this);
			}
		}
		
		/*EffectNode current = null;
			
		if (root instanceof ValueEffect) {
			current = ((ValueEffect) root).getEffects();
		}
		
		while (current != null) {
			if (current instanceof EffectKnown) {
				((EffectKnown) current).getEffect().run(this, root);;
				current = current.next;
			}else if (current instanceof EffectProbe){
				Value v = ((EffectProbe) current).getValue();
				current = current.next;
				
				for (int i = 0; i < this.memory.length; i++) {
					v = v.resolve(this.memory[i].probe, this.memory[i].value);
				}
				
				if (v instanceof ValueEffect) {
					EffectNode effects = ((ValueEffect) v).getEffects();
					
					if (effects != null) {
						current = effects.rebind(current);
					}
				}
			}
		}*/
	}
	
	public static class Resolve {
		public final Value value;
		public final ValueProbe probe;
		
		public final Resolve next;
		
		public Resolve(ValueProbe probe, Value value) {
			this(null, probe, value);
		}
		
		public Resolve (Resolve next, ValueProbe probe, Value value){
			this.next = next;
			this.probe = probe;
			this.value = value;
		}
		
		public Resolve useValue (Value value) {
			return new Resolve(this.probe, value);
		}
		
		public Resolve useProbe (ValueProbe probe) {
			return new Resolve (probe, this.value);
		}
		
		public Value resolve (Value v) {
			if (this.next != null) {
				v = this.next.resolve(v);
			}
			
			return v.resolve(this.probe, this.value);
		}
	}
}
