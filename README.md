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
