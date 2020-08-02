package parser.token.resolver;

import java.io.File;

public class EntrySource extends Source{
	public EntrySource(String name, File f) {
		super(name, f, true);
	}
}
