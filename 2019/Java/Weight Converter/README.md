# Weight Converter

A desktop application that allows the user to convert from american units (pounds) to european units(kilograms).

## Code summary
The application was created using java swing. <br/>
The components of the app are: the labels used for info, the textfields used for input, the calculate button and the clearance button. <br/>
The program asks for input from the user in order to use formulas for conversion. If all the fields contain information, the program will give an error and erase all the values.<br/>
All the textfields are formatted so that the only accepted form of input is by digits and by floating point.<br/>
The calculate button converts the given value to the desired unit. <br/>
The clear button removes the numbers from all fields.


## Complexity analysis
The algorithm consists of a series of ifs and formulas to determine the desider value. That results in the time complexity of ~O(1). <br/>
In terms of space utilised, the program contains simple variables used to store the application components => space complexity of ~O(1). <br/>
To sum everything up: time complexity - O(1), space complexity O(1). <br/>

## Illustration(s)

Convert to kg:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/Weight%20Converter/lbsToKg.png?raw=true)

Convert to lbs:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/Weight%20Converter/kgToLbs.png?raw=true)

Error message:

![image](https://github.com/Rares8921/Projects/blob/master/2019/Java/Weight%20Converter/errorMessage.png?raw=true)
