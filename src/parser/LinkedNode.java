package parser;

public class LinkedNode<K> {
	public final K value;
	public final LinkedNode<K> previous;
	
	public LinkedNode (K value) {
		this.value = value;
		this.previous = null;
	}
	
	private LinkedNode (K value, LinkedNode<K> previous) {
		this.value = value;
		this.previous = previous;
	}
	
	public LinkedNode<K> add (K value){
		return new LinkedNode<K>(value, this);
	}
	
	public LinkedNode<K> replace (K value){
		if (this.previous == null) {
			return new LinkedNode<K>(value);
		}else {
			return this.previous.add(value);
		}
	}
	
	public K get (int level) {
		if (level == 0) {
			return this.value;
		}else {
			return this.previous.get(level - 1);
		}
	}
}
