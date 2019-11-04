package parser.token;

public class TokenOperator extends TokenIdentifier{
	public TokenOperator (String key) {
		super(key);
	}
	
	@Override
	public boolean isModifier() {
		return false;
	}
}
