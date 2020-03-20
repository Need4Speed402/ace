package parser;

import java.util.ArrayList;

public class Promise <K> {
	private ArrayList<Getter<K>> getters;
	private K resolved;
	
	public Promise () {
		this.getters = new ArrayList<>();
		this.resolved = null;
	}
	
	public Promise (K resolved) {
		this.getters = null;
		this.resolved = resolved;
	}
	
	public void resolve (K value) {
		if (this.getters == null) throw new RuntimeException("already resolved");
		
		for (Getter<K> g : this.getters) {
			g.resolved(value);
		}
		
		this.getters = null;
		this.resolved = value;
	}
	
	public void resolve (Promise<K> value) {
		value.then(this::resolve);
	}
	
	public void then (Getter<K> getter) {
		if (this.getters == null) {
			getter.resolved(this.resolved);
		}else {
			this.getters.add(getter);
		}
	}
	
	public interface Getter<K> {
		public void resolved (K value);
	}
}
