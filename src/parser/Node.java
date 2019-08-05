package parser;

public class Node<K> {
	public final K value;
	public final Node<K> previous;
	
	public Node (K value) {
		this.value = value;
		this.previous = null;
	}
	
	private Node (K value, Node<K> previous) {
		this.value = value;
		this.previous = previous;
	}
	
	public Node<K> add (K value){
		return new Node<K>(value, this);
	}
	
	public Node<K> replace (K value){
		if (this.previous == null) {
			return new Node<K>(value);
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
