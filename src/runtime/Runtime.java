package runtime;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import value.Value;
import value.ValueEffect;
import value.ValuePartial.Probe;
import value.resolver.ResolverMutable;
import value.resolver.ResolverProbe;

public class Runtime {
	public final OutputStream out;
	public final InputStream in;

	private final HashMap<Probe, Value> memory = new HashMap<>();
	private final Runtime parent;
	public final Runtime root;

	public Runtime(OutputStream out, InputStream in) {
		this.out = out;
		this.in = in;

		this.parent = null;
		this.root = this;
	}

	public Runtime() {
		this(System.out, System.in);
	}

	private Runtime(Runtime r) {
		this.out = r.out;
		this.in = r.in;

		this.parent = r;
		this.root = r.root;
	}

	public int size() {
		return this.memory.size();
	}

	public Runtime push() {
		return new Runtime(this);
	}

	public Value extend(Runtime r, Value v) {
		for (Entry<Probe, Value> entry : r.memory.entrySet()) {
			Probe p = new Probe();
			v = v.resolve(new ResolverProbe(entry.getKey(), p));

			this.memory.put(p, entry.getValue());
		}

		return v;
	}

	public void set(Probe p, Value value) {
		Runtime current = this;

		while (current != null) {
			Value v = current.memory.get(p);

			if (v != null) {
				current.memory.put(p, value);
				return;
			}

			current = current.parent;
		}

		throw new Error("Cannot set: " + p + ". This indicates a bug with the interpreter");
	}

	public void declare(Probe p, Value value) {
		this.memory.put(p, value);
	}

	public Value get(Probe p) {
		Runtime current = this;

		while (current != null) {
			Value v = current.memory.get(p);

			if (v != null)
				return v;

			current = current.parent;
		}

		throw new Error("Cannot resolve: " + p + ". This indicates a bug with the interpreter");
	}

	public void run(Value root) {
		System.out.println(root);
		root = root.resolve(new ResolverMutable());
		
		if (root instanceof ValueEffect) {
			Effect[] effects = ((ValueEffect) root).getEffects();
			
			for (int i = 0; i < effects.length; i++) {
				effects[i].run(this);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString()).append('\n');

		for (Entry<Probe, Value> entry : this.memory.entrySet()) {
			b.append(entry.getKey() + " = " + entry.getValue() + '\n');
		}

		return b.toString();
	}
}
