Ace is a programming language that tries to redefine the conventions of
programming.

## Why
The main idea with this is to put the control back into the programmers hands.
This language is not trying to prevent you from doing things that are seen as
bad. In fact, if you wanted to, you could create code in this programming language
that overrides the + operator to instead multiply or change number literals ('123')
to create a boolean of some sort. But that's the thing, it gives you the power to
change the behavior of basic language features to do something completely different.

## How it works
In ACE, the whole programming language (other than compiler intrinsics which there
are many) can only do three things.
1. Call a function
2. Reference an identifier
3. Create an environment

In ACE there only exists one primitive: a function. When you call a function,
you must call the function with another function pointer. When you reference
an identifier, that identifier will always give you back a function. When
you create an environment, that is only a special case of a function.
It's unclear whether ace ACE would be categorized as a dynamic programming language
or a statically typed programming language. In statically typed programming languages,
the complier always knows the type. This is true in ACE because the type will always
be a function. But it could also be said to be a dynamically typed language because
there is no type checking on part of the compiler, and surely programs as if it were
dynamic. Now, this doesn't mean that when you start writing code in ACE, that it will
be dynamically typed. In fact types are very much a thing in ACE, not given to you
by the compiler but instead the standard library. Other ACE code is responsible for giving
the language types. This is the beauty in ace. If you have the power to express such a
complex thing in simple syntax, then many other things could be done with this as well
that does not involve adding features to the compiler and thus the programming language.

Identifiers in ace is the only way for the program to reference data.
But where does it get that data from? In other languages, you first need to
declare a variable using a keyword such as 'let' or a primitive type such as
'int' is c style languages. Instead ACE figures out what the value of an identifier
should be through its environment. Because it is possible to create a new 
environment in ace, an environment that is user controlled. It is possible that 
an environment gets created that completely changes what any of the identifiers do.
Because an environment is a function, it will be called with the identifier
that the scope that is using the environment. Consider this pseudo code:
```
environment = {
	console.log(foo)
	console.log(bar)
	console.log(baz)
}

environment (function(identifier) {
	//this is the special case for the fact that we are using console.log
	//we need to pass console to the parent environment for it to handle that
	//case.
	if (identifier == 'console') return identifier;

	if (identifier == 'foo') return "this is a the foo variable"
	if (identifier == 'bar') return "this is the bar variable"
	return "this is not an identifier I am familiar with"
})

```

Keep in mind that this code example in not written in ACE but a syntax highly
based off of JavaScript. On the first line, where we create and assign our
environment, this creates a function that we call later. When we call it,
we can call it with yet another function that controls the the value of all
the different identifiers that the environment can have access to. In this case,
foo, bar, and baz don't have any traditional definitions (let bar = ...), we can
still use them as if they do because the environment handles those.

## How do you build up more complex things
Cool, I can create functions, but what about integers, floats, strings and structures?
These are all supported types in ace, but compiler does not supply them, they are given
to you by the standard library and the type system. Consider the integer: 5. This is
syntactic sugar for something like this:
```Integer [false; true; false; true]```
The parser will call the Integer constructor with a list of binary bits basically.
The standard library of course defines what Integer is and creates a function that
can be thought of as an object and works like an object. The natural question after this
is: okay, how is the compiler defining what an array is or what true and false mean?
This is something that took me a year to figure out with the language design is how to 
bootstrap it. The above example about the Integer is actually a gross simplification
and uses those three tools give to you above and some cleaver compiler intrinsics.
This topic very much goes into the mathematics of functional programming and this is
outside of the scope of this introduction.