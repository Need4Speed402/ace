package value.intrinsic;

import value.Value;
import value.ValueFunction;
import value.ValuePartial;

public class Function extends ValuePartial{
	public static final Value instance = new ValueFunction(name -> new CallReturn (new ValueFunction(body -> new CallReturn(new ValueFunction(arg -> body.call(new ValueFunction(envName -> {
		// 'name' is the identifier that the function is declared with
		// 'envName' comes from the environment
		// 'arg' is the argument of the function
		//Basically, for all the occurances of an identifier that appears
		// in the function body will be replaced by the value given by 'arg'
		// everything else will be filtered through to the parent environment, 'envName'
		return new CallReturn(Compare.create(name, envName, envName, arg));
	})))))));
}
