package event;

import parser.Local;
import parser.token.TokenFunction;
import value.Value;

public class EventHiddenFunction extends EventScope{
	public EventHiddenFunction (Event contents) {
		super(contents);
	}
	
	@Override
	public Value run(Local local) {
		return new Value(p -> super.run(local, p));
	}
	
	@Override
	public String toString() {
		return "{\n" + TokenFunction.indent(this.contents.toString()) + "\n}";
	}
}