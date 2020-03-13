package parser.token;

public abstract class Resolver implements Token{
	private final String name;
	
	public Resolver (String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
