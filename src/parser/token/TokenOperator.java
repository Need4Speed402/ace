package parser.token;

public class TokenOperator extends TokenIdentifier{
	public TokenOperator (String key) {
		super(key);
	}
	
	public boolean isSetter () {
		if (this.id.length() == 0) return false;
		if (this.id.charAt(0) != '=') return false;
		
		for (int i = 1; i < this.id.length(); i++) {
			char c = this.id.charAt(i);
			
			if (c == '=') return false;
		}
		
		return true;
	}
	
	@Override
	public boolean isModifier() {
		return false;
	}
}
