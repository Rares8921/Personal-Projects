# String Info

A desktop application that provides information about a certain string, specified by the user.

## Code summary
The application was created using java swing.<br/>
The components of the app are: The menus: file, help and history, the textField (the whitespaces are removed, and special characters are not allowed - regex), the enter action button. After pressing the button, there will be additional labels that contains information about the string. <br/>
The file menu is used for refreshing the components or closing the app; the help menu provides more guidance about the usage of the application; the history menu gives the last input of the user, and, in order to delete the history, the user has to pass a mini-captcha. <br/>
The program requires a text input from the user to analise. The information provided will contain the number of characters/digits(using exceptions), and, in case of strings, the number of vowels(got from a linear function). <br/>


## Complexity analysis
Let s be the string input.
The replaceAll function for textfield(regex) - O(|s|) time complexity. <br/>
The vowelCount function - O(|s|) time complexity. <br/>
The program contains a few variables for the app components, and a string for input: s. That results in O(|s|) space complexity. <br/>
To sum everything up: O(|s|) space and time complexity.

## Requirement(s)
Java JRE 8 or a later version installed.

## Illustration(s)

Normal text:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/text.png?raw=true)

Number:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/number.png?raw=true)

History menu:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/historyMenu.png?raw=true)
![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/historyMenudelete.png?raw=true)
![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/historyMenuSuccesulClear.png?raw=true)
![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/historyMenuError.png?raw=true)

Help menu:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/helpMenu.png?raw=true)
![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/helpMenuInfo.png?raw=true)
![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/helpMenuSpecialCharacters.png?raw=true)

File menu:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/fileMenu.png?raw=true)

Exception message:
![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/String%20Info/exceptionMessage.png?raw=true)
