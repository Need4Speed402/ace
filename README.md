Ace is a programming language that tries to redefine the conventions of
programming.

## Why
All programming languages consists of two things: axioms, features
in which the entire programming language is built upon. Without at least one
built in feature, the language can not exist. Second are user defined functions
and ways to define them. Technically it is possible to build programs without
defining anything from the user, but the mental burden of keeping an accurate
mental model of the program becomes near impossible without abstractions.
Especially as the minimum complexity of a program increases.

Ignoring the prospect of a standard library, every programming language has to
decide what features or axioms to include into their language to be useful
but it is also important to understand that adding new features has a cost in
terms of complexity, ease of understanding syntax and compilation time.
The unwise may assume that more is better, but this logic is fundamentally
flawed as it is not possible to implement every feature to solve every possible
problem so there needs to be a balance of features. Add useful ones and leave
out others because it is deemed that their inclusion will raise the complexity
to a level that is not justified by its potential utility.

## Whats wrong with *my programming language*
Chances are you are using a language which is a derivative of C and more or less
blindly follows C's syntax such as how numerical literals are expressed,
the idea of an expression vs a statement and using {} () to denote control flow.
C was designed to compile 1:1 to machine instructions and provided syntax that
looked more like mathematical equations. Of course computers are more complex
and provide control flow, i/o and mutability all of which programming languages
have to handle so pure mathematics notation cannot be used. C provides
operators to mutate and operate on primitive values, control flow statements
such as if and while to conditionally run or re-run parts of the program.
Primitive values such as chars, shorts, ints, longs, floats were added to map
to values that the processor could directly execute on. All this was added
and based upon the machine architecture of the time. All of it based on the
architecture being compiled to. C is a higher level of abstraction on top of
assembly, which differs from architecture to architecture. Now it is important
to creating launguages and tools that are platform agnostic even if the tradeoff
is speed. Unfortunantely, even higher level languages such as JavaScript have
syntax that is purely defined from lower level details like the IEEE floating point
number spec that defines all numbers, and most other systax being derived from lower
level languages where their goals are completely different. ACE was designed from
the ground up to think logically to help you build abstractions more powerful
then is possible in all mainstream languages.

## What ACE tries to do
ACE is a programming language which tries to abstract even the most basic
ideas such as mutability, the concept of an integer, or a boolean. Backing
these concepts behind programmer definable construct is ideal so that the
programmer can choose case-by-case what features will best suit their project
and what features can be left out. This is to avoid unnecessary complexity that
will only aid in programmer confusion. To you it may not make sense to cry about
the inclusion of a 32 bit integer being arbitrary, but this is about the idea.
Try to apply it to the general. Suppose my boss comes in and tells me that my
64 bit integer has overflowed; unlikely but possible. Since integers are user
defined, it is possible to drop in a 128 bit integer to be used without much
code modification even if the programming language does not support a 128 bit
value natively.

## Syntax
ACE's syntax tries to be as simple as possible with the goal of abstractions
being easily built upon these few but powerful tools.

Name | Example Syntax | Explanation
---- | ------ | -----------
Function | {} | Creating a Function, paramater exposed as .
Modifier Function | Class {} | If a function follows a modifier, the paramater is then exposed as :
Operator Function | * {} | If a function follows an operator, the paramater of the function is not exposed
Scope | () | Runs like a iife, creates a new variable scope and can be used to reorder code execution
Array | [] | Creates a new immutable list of elements by calling Array in the current scope
String | '' | Creates a simple string, escape character is \`.
Escaped String | "\`" | Creates a string where the delimiter to get out is \`"
Integer | 1,2,3 | A string of numerical characters that can optionally be separated by ,
Hex Integer | 10^ | A string of numerical charaters that terminates in ^
Binary Integer | 10! | A string of numerical characters that terminates in !
Octal Integer | 10* | A string of numerical characters that terminates in *
Float | 1,2,3.1,2,3 | A string of numerical charaters that contains a . somewhere inside floats can also terminate in either: [.!^*] to change base
Modifier | Class | Any identifier that starts with a upper-case charater will have different precedence rules
Operator | * | Any identifier that contains any of the characters: [.: ?,~_ @ \|&! =<> +- */\\% ^ $# \`] has different precedence rules, and is reffered to as a operator identifier
Setter Operator | = | Any operator that starts with = and contains no other = charaters will be an identifier that has a Right-to-Left associativity
Identifier | abc | Any other string of charaters
Unary operator | >var | Any set of operator characters that precedes an identifier
Function application | a b | Any whitespace between two valid syntax contructs will call function a with paramater b
Immediate function application | a(b) | If there is no whitespace between two distinct syntax constructs, a will be called with b with the highest precedence
Immediate paramater application | .a :b | If a syntax contruct is proceeded with . or : the paramater will be called with the value of that syntax with the highest precedence
Single line comment | ;; hello | A single line comment dilimiter is ```;;;```. Whitespace can also be used in place of the last semicolon. The comment will terminate upon a newline
Statement comment | ;;{} | A statement comment starts with ```;;``` and will parse a statement but not execute it.

## Functions
Since the basis of everything in ACE is a function, the syntax to create a function is
really simple.\
The syntax uses curly braces ```{ <code> }``` to denote a funciton. Functions always accept
one paramater and return one paramater. If a function runs to the end of its execuption
the null function will be returned. Typically, the paramater will be exposed as ```.```.\
```{}``` is the null function where nothing is done with
the pramater in implicitly it will return itself. The null function does have a large role in
the language as it controls code execution.\
Consider this:
```
	{
		run one
		run two
		run three
	}
```

There are three statements in this function, and they will be executed in order until one of them does not return null
suppose ```run one``` gets executed and returns null, ```run two``` will then be run. Suppose that function returns the integer 1.
Code will stop executing there in the function and 1 will be returned. This is called implicit returns. This is done for two reasons,
first error handling is trivial. If I had a function that if run succesfully would return null, everything would be dandy and code will
continue to execute, the suppose there was an error, since there is no try/catch in ACE, errors will have to be
returned like all regular values, and in the case of the function regularly returning null, the error will be propogated down the call stack
as if the error was thrown. The second reason why this is done is to solve a problem with this case
```
	{
		>value = 6

		value == 6 ? {
			'value is 6'
		}

		'value is not 6'
	}
```
Here is a function with a conditional, first I am setting a local variable called value and initializing it to 6 then comparing the value to 6.
Ofcorse in this example the conditional will always execute. And in the case of the callback for the conditional, it will return a string.
If implimit returns did not exist, the code would be very verbose where I would need to place a ```return``` in every concievable spot where I
would want to return my value, even then, there is no way to conditionally return from a function.\
The second major feature to functions is the ability to accept information. Typically this paramater is exposed as ```.```.\
Example: ```>addOne = {. + 1}```. This syntax exists so that it is really convenient to make lamba functions, one of the most
common code patterns in ACE. ```.``` is also a special case for the compiler where it knows that you want to get the paramater,
this is why you don't assign a name to the paramater. If it were assigned a name, that paramater would then be exposed as an identifier
and the whole basis to property access in ACE is to pass identifiers to functions, that function has to know what identifier was passed
to it, by assiging it to an identifier, that information will be lost. And so this is why ```.``` is used as it is a special case for the compiler.
Under most circumstances, the paramater will be exposed as ```.``` but is not always the case. If a modifier proceeds the function, the paramater will
then be exposed as ```:```. It is typical in programming where you are working on two different kinds of data, the data that you want to mutate in
response to other incoming data. : will store the data you want to mutate and . will be the data that controls the mutation. Consider the case for classes:
```
	Class {
		:this addToArgument {
			: + .
		}
	}
```
```:``` will store the class instance, while ```.``` will store any temporary data.\
Lastly, there is a third case for paramaters. When a function follows an operator, the paramater will not be exposed at all. Consider the second example of this section
that used the conditional with the callback, that callback is usually defined in-line with the rest of the conditional. Exposing the paramater in this
case would be useless as the conditional cannot give any useful information to the callback, so to cover this case, the paramater is not exposed if proceeded by an operator.
