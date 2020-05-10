package value.effect;

import java.io.PrintStream;

import value.Value;
import value.ValueEffect;
import value.ValueProbe;
import value.ValueEffect.EffectNode;
import value.ValueEffect.EffectNodeList;
import value.ValueEffect.EffectNodeValue;
import value.ValueProbe.Resolve;

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
		EffectNode current = new EffectNodeValue(null, root);
			
		while (current != null) {
			if (current instanceof EffectNodeList) {
				((EffectNodeList) current).getEffect().run(this, root);;
				current = current.next;
			}else if (current instanceof EffectNodeValue){
				Value v = ((EffectNodeValue) current).getValue();
				current = current.next;
				
				for (int i = 0; i < this.memory.length; i++) {
					v = v.resolve(this.memory[i].probe, this.memory[i].value);
				}
				
				if (v instanceof ValueEffect) {
					current = ((ValueEffect) v).getEffects().rebind(current);
				}
			}
		}
	}
}
