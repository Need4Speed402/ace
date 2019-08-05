package parser.token;

import event.Event;

public abstract class Token {
	public abstract Event createEvent ();
}
