package parser.token;

public class TokenBase extends TokenCompound{
	@Override
	public String toString() {
		return toString(this, '\n');
	}
}
