package event;

import parser.Local;
import unsafe.Memory;
import value.Value;

public class EventList extends EventCompound{
	public EventList (Event[] items) {
		super(items);
	}
	
	@Override
	public Value run(Local local) {
		Value[] arr = new Value[this.events.length];
		
		for (int i = 0; i < this.events.length; i++) {
			arr[i] = this.events[i].run(local);
		}
		
		return new Memory.Allocate(arr);
	}
}
