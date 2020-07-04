package value.effect;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import value.Value;
import value.ValuePartial.Probe;

public class Runtime {
	public final OutputStream out;
	public final InputStream in;
	
	private Memory memory = new Memory(null);
	
	public Runtime(OutputStream out, InputStream in) {
		this.out = out;
		this.in = in;
	}
	
	public Runtime () {
		this.out = System.out;
		this.in = System.in;
	}
	
	
	public void push () {
		this.memory = new Memory(this.memory);
	}
	
	public Value pop (Value v) {
		HashMap<Probe, Reference> c = this.memory.memory;
		this.memory = this.memory.previous;
		
		for (Entry<Probe, Reference> entry: c.entrySet()) {
			if (entry.getValue() != this.memory.memory.get(entry.getKey())) {
				Probe p = new Probe();
				
				v = v.resolve(entry.getKey(), p);
				
				this.memory.memory.put(p, entry.getValue());
			}
		}
		
		return v;
	}
	
	public void set (Probe p, Value value) {
		Reference r = this.memory.memory.get(p);
		if (r == null) throw new Error("Cannot set: " + p + ". This indicates a bug with the interpreter");
		
		r.value = value;
	}
	
	public void declare (Probe p, Value value) {
		this.memory.memory.put(p, new Reference(value));
	}
	
	public Value get (Probe p) {
		Reference r = this.memory.memory.get(p);
		if (r == null) throw new Error("Cannot resolve: " + p + ". This indicates a bug with the interpreter");
		
		return r.value;
	}
	
	public void run (Value root) {
		//System.out.println(root);
		root.run(this);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString()).append('\n');
		
		for (Entry<Probe, Reference> entry : this.memory.memory.entrySet()){
			b.append(entry.getKey() + " = " + entry.getValue().value + '\n');
		}
		
		return b.toString();
	}
	
	private static class Reference {
		public Value value;
		
		public Reference (Value value) {
			this.value = value;
		}
	}
	
	private static class Memory {
		public final HashMap<Probe, Reference> memory = new HashMap<>();
		public final Memory previous;
		
		public Memory (Memory previous) {
			this.previous = previous;
			
			if (previous != null) for (Entry<Probe, Reference> entry : previous.memory.entrySet()){
				this.memory.put(entry.getKey(), entry.getValue());
			}
		}
	}
}
