#!/usr/bin/python3.7.1

# Working on pycharm

import random

HEADER = '\033[95m'
BLUE = '\033[94m'
GREEN = '\033[92m'
WARNING = '\033[93m'
MAGENTA = '\033[91m'
WHITE = '\033[0m'
BOLD = '\033[1m'
UNDERLINE = '\033[4m'


a = random.randint(1, 6)
b = random.randint(1, 6)


print(WHITE + "\n[Roll dice] - First who get 2 points is the winner.")
game = str(input(WHITE + "If you want to play, type 'roll'; if u want to quit, type 'exit': "))


if game == 'exit':
    print(WHITE + "Stopping the game..")
elif game == 'roll':
    print(WHITE + "\nRolling..")
    print("Your values:" + BLUE, a, b)

    c = random.randint(1, 6)
    d = random.randint(1, 6)

    sum1 = a + b
    sum2 = c + d

    print(WHITE + "Computer's values:" + BLUE, c, d)

    if sum1 > sum2:
        print(WHITE + "You got 1 point.\nYou: " + GREEN + "1\n" + WHITE + "Computer: " + GREEN + "0\n" + WHITE)
        print("\nRolling..")
        e = random.randint(1, 6)
        f = random.randint(1, 6)
        print("Your values:" + BLUE, e, f)
        g = random.randint(1, 6)
        h = random.randint(1, 6)
        print(WHITE + "Computers values:" + BLUE, g, h)
        sum1 = e + f
        sum2 = g + h
        if sum1 > sum2:
            print(WHITE + "You got 1 point.\nYou: " + GREEN + "2\n" + WHITE + "Computer: " + GREEN + "0\n" + WHITE)
            print("\nYou are the winner. Congratulations!")
        elif sum1 < sum2:
            print(WHITE + "Computer got 1 point.\nYou: " + GREEN + "1\n" + WHITE + "Computer: " + GREEN + "1\n" + WHITE)
            i = random.randint(1, 6)
            j = random.randint(1, 6)
            k = random.randint(1, 6)
            l = random.randint(1, 6)
            print("\nRolling...")
            print("Your values: " + BLUE, i, j)
            print(WHITE + "Computer's values: " + BLUE, k, l)
            sum1 = i + j
            sum2 = k + l
            if sum1 > sum2:
                print(WHITE + "You got 1 point.\nYou: " + GREEN + "2\n" + WHITE + "Computer: " + GREEN + "1\n" + WHITE)
                print("\nYou are the winner. Congratulations!")
            elif sum1 < sum2:
                print(WHITE + "Computer got 1 point.\nYou: " + GREEN + "1\n" + WHITE + "Computer: " + GREEN + "2\n" + WHITE)
                print("\nThe winner is '" + MAGENTA + "computer" + WHITE + "'")
            elif sum1 == sum2:
                print(WHITE + "The values are equal, so everyone gets 1 point.\nYou: " + GREEN + "2\n" + WHITE + "Computer: " + GREEN + "2\n" + WHITE)
                print("\nThere is no winner. Maybe next time. :(")
        elif sum1 == sum2:
            print(WHITE + "The values are equal, so everyone gets 1 point.\nYou: " + GREEN + "2\n" + WHITE + "Computer: " + GREEN + "1\n" + WHITE)
            print("\nYou are the winner. Congratulations!")
    elif sum1 < sum2:
        print(WHITE + "Computer got 1 point.\nYou: " + GREEN + "0\n" + WHITE + "Computer: " + GREEN + "1\n" + WHITE)
        print("\nRolling..")
        e = random.randint(1, 6)
        f = random.randint(1, 6)
        print("Your values:" + BLUE, e, f)
        g = random.randint(1, 6)
        h = random.randint(1, 6)
        print(WHITE + "Computers values:" + BLUE, g, h)
        sum1 = e + f
        sum2 = g + h
        if sum1 > sum2:
            print(WHITE + "You got 1 point.\nYou: " + GREEN + "1\n" + WHITE + "Computer: " + GREEN + "1\n" + WHITE)
            i = random.randint(1, 6)
            j = random.randint(1, 6)
            k = random.randint(1, 6)
            l = random.randint(1, 6)
            print("\nRolling...")
            print("Your values: " + BLUE, i, j)
            print(WHITE + "Computer's values: " + BLUE, k, l)
            sum1 = i + j
            sum2 = k + l
            if sum1 > sum2:
                print(WHITE + "You got 1 point.\nYou: " + GREEN + "2\n" + WHITE + "Computer: " + GREEN + "1\n" + WHITE)
                print("\nYou are the winner. Congratulations!")
            elif sum1 < sum2:
                print(WHITE + "Computer got 1 point.\nYou: " + GREEN + "1\n" + WHITE + "Computer: " + GREEN + "2\n" + WHITE)
                print("\nThe winner is '" + MAGENTA + "computer" + WHITE + "'")
            elif sum1 == sum2:
                print(WHITE + "The values are equal, so everyone gets 1 point.\nYou: " + GREEN + "2\n" + WHITE + "Computer: " + GREEN + "2\n" + WHITE)
                print("\nThere is no winner. Maybe next time. :(")
        elif sum1 < sum2:
            print(WHITE + "Computer got 1 point.\nYou: " + GREEN + "0\n" + WHITE + "Computer: " + GREEN + "2\n" + WHITE)
            print("\nThe winner is '" + MAGENTA + "computer" + WHITE + "'")
        elif sum1 == sum2:
            print(WHITE + "The values are equal, so everyone gets 1 point.\nYou: " + GREEN + "1\n" + WHITE + "Computer: " + GREEN + "2\n" + WHITE)
            print("\nThe winner is '" + MAGENTA + "computer" + WHITE + "'")
    elif sum1 == sum2:
        print(WHITE + "The values are equal, so everyone gets 1 point.\nYou: " + GREEN + "1\n" + WHITE + "Computer: " + GREEN + "1\n" + WHITE)
        print("\nRolling..")
        e = random.randint(1, 6)
        f = random.randint(1, 6)
        print("Your values:" + BLUE, e, f)
        g = random.randint(1, 6)
        h = random.randint(1, 6)
        print(WHITE + "Computers values:" + BLUE, g, h)
        sum1 = e + f
        sum2 = g + h
        if sum1 > sum2:
            print(WHITE + "You got 1 point.\nYou: " + GREEN + "2\n" + WHITE + "Computer: " + GREEN + "1\n" + WHITE)
            print("\nYou are the winner. Congratulations!")
        elif sum1 < sum2:
            print(WHITE + "Computer got 1 point.\nYou: " + GREEN + "1\n" + WHITE + "Computer: " + GREEN + "2\n" + WHITE)
            print("\nThe winner is '" + MAGENTA + "computer" + WHITE + "'")
        elif sum1 == sum2:
            print(WHITE + "The values are equal, so everyone gets 1 point.\nYou: " + GREEN + "2\n" + WHITE + "Computer: " + GREEN + "2\n" + WHITE)
            print("\nThere is no winner. Maybe next time. :(")
else:
    print(WHITE + "Unknown command. The program will stop..\n")
