Ace is a programming language that tries to redefine the conventions of
programming.

## Why
All programming languages consists of two things: axioms, features
in which the entire programming language is built upon. Without at least one
built in feature, the language can not exist. Second are user defined functions
and ways to define them. Technically it is possible to build programs without
defining anything from the user, but the mental burden of keeping an accurate
mental model of the program becomes near impossible without abstractions.
Especially as the minimum complexity of a program increases

Ignoring the prospect of a standard library, every programming language has to
decide what features or axioms to include into their language to be useful
but it is also important to understand that adding new features has a cost in
terms of complexity, ease of understanding syntax and compilation time.
The unwise may assume that more is better, but this logic is fundamentally
flawed as it is not possible to implement every feature to solve every possible
problem so there needs to be a balance of features to add and ones that ought
be be left out because it is deemed that their inclusion will raise the
complexity to a level that is not justified by its potential utility.

## Whats wrong with *my programming language*
Chances are you are using a language which is a derivative of C and more or less
blindly follows C's syntax such as how numerical literals are expressed,
the idea of an expression vs a statement and using {} () to denote control flow.
C was designed to compile 1:1 to machine instructions and provided syntax that
looked more like mathematical equations. Of course computers are more complex
and provide control flow, i/o and mutability all principals that mathematics
did not consider and should not consider as they are details. C provides
operators to mutate and operate on primitive values, control flow statements
such as if and while to conditionally run or re-run parts of the program.
Primitive values such as chars, shorts, ints, longs, floats were added to map
to values that the processor could directly execute on. All this was added
and based upon the machine architecture of the time. All of it completely
arbitrary. C assumes that programmers only need a hand full of data types,
a few control flow statements and maybe sprinkle in a class here and there.
The best programming language for a given task is one that was designed for your
task or best suited for your task. A 64 bit unsigned integer may be relevant
today but tomorrow we might need a 128 bit integers.

## What ACE tries to do
ACE is a programming language which tries to abstract even the most basic
ideas such as mutability, the concept of an integer, or a boolean behind a
programmer definable construct so that the programmer can choose case-by-case
what features will best suit their project and what features can be left out
to avoid unnecessary complexity that will only aid programmer confusion to the
kind of control that the operators provided by the basic integer can be fine
tuned. To you it may not make sense to cry about the inclusion of a 32 bit
integer being arbitrary, but this is about the idea. Try to apply it to the
general. Suppose my boss comes in and tells me that my 64 bit integer has
overflowed; unlikely but possible. Since integers are user defined, it is
possible to drop in a 128 bit integer to be used without much code modification
even if the programming language does not support a 128 bit value natively.