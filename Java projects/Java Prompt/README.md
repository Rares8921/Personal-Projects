# Java Prompt

A static desktop application designed as a mini command prompt, that allows the user to run a few commands to get info about their configurations, to access the browser etc.

## Code summary
The application was created using java swing library. <br/>
All the commands are shown to user by dialogue boxes. <br/>
The program asks the user to type a command using the textfield. If the command is correct, the program will run sucessfuly. If the command is unknown, the program will throw an error mesage. </br>
For the info command, all the java information is gathered using the System class. <br/>
The url command uses the Desktop class to connect to the default browser, selected by the user, in order to search the specified url. <br/>
The prime command utilises a simple algorithm( with time compleity of O(sqrt(n)), where n is effectively 100 ). <br/>
The date is collected using the LocalDate class. <br/>


## Requirement(s)
Mandatory: Java JRE 8 or a later version installed. <br/>
Optional: Internet connection (for url command).

## Illustration(s)

Main stage:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/Java%20Prompt/mainFrame.png?raw=true)

Java info:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/Java%20Prompt/javaInfoCommand.png?raw=true)

Url command:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/Java%20Prompt/urlCommand.png?raw=true)

Prime numbers:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/Java%20Prompt/primeNumbersCommand.png?raw=true)

Date command:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/Java%20Prompt/errorMessage.png?raw=true)

Error message:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/Java%20Prompt/errorMessage.png?raw=true)
