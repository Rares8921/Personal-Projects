# Decimal Converter Calculator

A desktop tap designed like a normal calculator, but its purpose its to transform a certain number(base 10) to a diffrent form: binary, octal or hexadecimal. The user shall give a number with at most 18 digits to input; both the buttons in the applications or the keyboard could be used to type. The given number must be an integer.

## Code summary

The application was created using java swing. <br/>
The components of the app are: the textview(where the input goes), the numerical buttons(used for typing the input), the clearance button, the deletion button, the transformation buttons and the alerts for each base conversion. <br/>
In the textview, the user is only allowed to type digits. The program will automaticaly stop the user from typing tyhe input if an error was occured. <br/>
When the user presses a transformation button, and the input is specified, it is used an algorithm with logarithmic complexity to transform a number to a certain base, then it is printed in an alert box. <br/>

## Complexity analysis

Time complexity of conveting a number to binary is O(log2(n)), to base-8 is O(log8(n)) and to hex. is O(log16(n)). That results in the final time complexity of O(log2(n) + log8(n) + log16(n)) = O(max(log2(n), log8(n), log16(n))) = O(log2(n)) <br/>
In terms of space complexity, there are a bunch of button variables and a textview(O(1)), together with an array used in each transformation of the number(O(l), where l is the length of the given number). <br/>
To sum everything up, time complexity: O(log2(n)); space complexity: O(l). <br>

## Requirement(s)

Java JRE 8 or a later version installed.

## Illustration(s)

Main page:

![image]()

Binary conversion:

![image]()

Base-8 conversion:

![image]()

Hex. conversion:

![image]()