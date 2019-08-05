package event;

import java.util.List;

import parser.Local;
import value.Value;

public class EventDynamic implements Event{
	//private final static HashMap<String, Integer> indexMapping = new HashMap<String, Integer>();
	//private final static ArrayList<Value> globalMapping = new ArrayList<Value>();
	
	//public static void registerMapping(String v, )
	
	private Value cache;
	private final Create create;
	
	public EventDynamic (Create create) {
		this.create = create;
	}

	@Override
	public Value run(Local local) {
		if (this.cache == null) {
			this.cache = create.create();
		}
		
		return this.cache;
	}

	@Override
	public void indexIdentifiers(EventScope scope, List<EventIdentifier> idnt) {}
	
	public interface Create {
		public Value create ();
	}
}
