package runtime;

import java.io.InputStream;
import java.io.OutputStream;

import value.resolver.ResolverMutable;

public class Runtime {
	public final OutputStream out;
	public final InputStream in;

	public Runtime(OutputStream out, InputStream in) {
		this.out = out;
		this.in = in;
	}

	public Runtime() {
		this(System.out, System.in);
	}

	public void run(Effect program) {
		System.out.println(program);
		Effect programFinished = new ResolverMutable().resolveEffect(program);
		System.out.println(programFinished);
		programFinished.run(this);
	}

	@Override
	public String toString() {
		return "Runtime";
	}
}
