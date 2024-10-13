#!/usr/bin/python3.7.1

import random

a = random.randint(1, 100)

b = int(input("\nGuess the number (from 1 to 100). You have 3 chances left: "))

if a > b:
    print("Nope, but the number is bigger than %i" % b)
    c = int(input("\nGuess the number. You have 2 chances left: "))
    if a > c:
        print("Nope, but the number is bigger than %i" % c)
        d = int(input("\nGuess the number. You have 1 chance left: "))
        if a > d:
            print("\nNope. The number is: %i" % a)
        elif a < d:
            print("\nNope. The number is: %i" % a)
        elif a == d:
            print("\nCongratulations! You guessed the number ( ", a, " )")
    elif a < c:
        print("Nope, but the number is less than %i" % c)
        d = int(input("\nGuess the number. You have 1 chance left: "))
        if a > d:
            print("\nNope. The number is: %i" % a)
        elif a < d:
            print("\nNope. The number is: %i" % a)
        elif a == d:
            print("\nCongratulations! You guessed the number ( ", a, " )")
    elif a == c:
        print("\nCongratulations! You guessed the number ( ", a, " )")
elif a < b:
    print("Nope, but the number is less than %i" % b)
    c = int(input("\nGuess the number. You have 2 chances left: "))
    if a > c:
        print("Nope, but the number is bigger than %i" % c)
        d = int(input("\nGuess the number. You have 1 chance left: "))
        if a > d:
            print("\nNope. The number is: %i" % a)
        elif a < d:
            print("\nNope. The number is: %i" % a)
        elif a == d:
            print("Congratulations! You guessed the number ( ", a, " )")
    elif a < c:
        print("Nope, but the number is less than %i" % c)
        d = int(input("\nGuess the number. You have 1 chance left: "))
        if a > d:
            print("\nNope. The number is: %i" % a)
        elif a < d:
            print("\nNope. The number is: %i" % a)
        elif a == d:
            print("\nCongratulations! You guessed the number ( ", a, " )")
    elif a == c:
        print("\nCongratulations! You guessed the number")
elif a == b:
    print("\nCongratulations! You guessed the number")

