package value.effect;

import java.io.PrintStream;

import value.Value;
import value.ValuePartial.Probe;

public class Runtime {
	public final PrintStream out;
	public Resolve memory = null;
	
	public Runtime(PrintStream stream) {
		this.out = stream;
	}
	
	public Runtime () {
		this.out = System.out;
	}
	
	public Runtime(Runtime run) {
		this.out = run.out;
		this.memory = run.memory;
	}
	
	public void setResolve (Probe probe, Value value) {
		this.setResolve(new Resolve(probe, value));
	}
	
	public void setResolve (Resolve r) {
		while (r != null) {
			Resolve rep = this.memory == null ? null : this.memory.replace(r.probe, r.value);
			
			if (rep == this.memory) {
				this.memory = new Resolve(rep, r.probe, r.value);
			}else {
				this.memory = rep;
			}
			
			r = r.next;
		}
	}
	
	public void run (Value root) {
		System.out.println(root);
		
		root.run(this);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString()).append('\n');
		
		Resolve current = this.memory;
		
		while (current != null) {
			b.append(current).append('\n');
			
			current = current.next;
		}
		
		return b.toString();
	}
	
	public static class Resolve {
		public final Value value;
		public final Probe probe;
		
		public final Resolve next;
		
		public Resolve(Probe probe, Value value) {
			this(null, probe, value);
		}
		
		public Resolve (Resolve next, Probe probe, Value value){
			this.next = next;
			this.probe = probe;
			this.value = value;
		}
		
		public Resolve replace (Probe probe, Value value) {
			if (this.probe == probe) {
				return new Resolve(this.next, probe, value);
			}else if (this.next != null) {
				Resolve rep = this.next.replace(probe, value);
				
				if (rep == this.next) {
					return this;
				}else {
					return new Resolve(rep, this.probe, this.value);
				}
			}else {
				return this;
			}
		}
		
		@Override
		public String toString() {
			return this.probe.toString() + "=" + this.value.toString();
		}
	}
}
