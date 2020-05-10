package value.effect;

import value.Value;

public interface Effect {
	public final Effect[] NO_EFFECTS = new Effect [] {};
	
	public void run (Runtime runtime, Value root);
}
