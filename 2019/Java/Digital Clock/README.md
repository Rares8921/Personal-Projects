# C++ Command prompt

C++ - prompt is a mini-clone of windows command prompt, with a bunch of commands that give the user the possibility to work with numbers, to check some information about the computer (or to give it some tasks) or to use the internet to browse about a desired subject. The commands are case insensitive and they must be typed with the "!" prefix.

## Code summary

Firstly, the user is asked to specify a command and the program checks if the given input is valid to perform a task. <br/>
The prime command uses a standard prime number verification algorithm. <br/>
The time command gives the current date and hour using the time() function to extract the information. The data is formatted using strings (DD.MM.YYYY for current day, and H.MIN for the current time). <br/>
The ip and ping commands require to run the app as an administrator.  The algorithm uses the ipconfig command through the system function and creates a text file called "ip.txt" where the information is extracted. After, the additional information is deleted and the algorithm keeps only the ip or the current ping. <br/>
The connect command asks the user to give and url and then opens their default browser with the link. The command requires internet connection. <br/>
The info command gives information about the cpu and the ram of the computer. The program extracts the information through hexadecimal notation. <br/>

## Complexity analysis
The time complexity of prime function is O(sqrt(n)), where n is the number given by the user, and n is an int variable. So, the max number of repetitions is 46341. <br/>
Time complexity for !ip and !ping commands are O(s), where s is the length of the read file(s is about 900 characters long. <br/>
In the prorgram, there are several simple variables used => the space complexity is ≈O(1). <br/>
Therefore, the final time complexity is O(sqrt(n) + O(s)) = O(max(sqrt(n), s)) = O(sqrt(n)); the space complexity ≈O(1). <br/>

## Requirement(s)
Mandatory: Windows 10 or lower. <br/>
Optional: Administrator properties(for !ip and !ping) and internet connection(for !browse).

## Illustration(s)

![image]()