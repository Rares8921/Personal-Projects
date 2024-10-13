import pyshorteners
from urllib.request import urlopen


def link_shortener(link):
    shortener = pyshorteners.Shortener()
    short_link = shortener.tinyurl.short(link)
    print('Original Link: ' + link)
    print('Shortened Link: ' + short_link)


def link_opener(link):
    shortened_url = urlopen(link)
    real_link = shortened_url.geturl()  # getting real link
    print('Shortened Link: ' + link)
    print('Real Link: ' + real_link)


num = input("1. Type 1 for shortening the link\n"
            "2. Type 2 for extracting the real link from a shortened link\n")
link = input("Enter the link: ")
if num == '1':
    link_shortener(link)
else:
    link_opener(link)
