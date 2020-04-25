package value.effect;

import java.io.PrintStream;

public class Runtime {
	public final PrintStream out;
	
	public Runtime(PrintStream stream) {
		this.out = stream;
	}
	
	public Runtime () {
		this.out = System.out;
	}
}
