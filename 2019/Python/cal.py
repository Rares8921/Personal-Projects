#!/usr/bin/python3.7

import calendar

GREEN = '\033[92m'

y = int(input("Input the year: "))
m = int(input("Input the month (from 1 to 12): "))

print("\n" + GREEN, calendar.month(y, m))
