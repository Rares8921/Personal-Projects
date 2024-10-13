# Link Shortener and Opener Script

A Python script that provides functionality to shorten a given link using the `pyshorteners` library and to extract the real link from a shortened link using the `urllib.request` library.

## Code summary

Firstly, the necessary libraries `pyshorteners` and `urllib.request` are imported. The script defines two main functions:

1. `link_shortener(link)`: This function takes a URL as input, uses the `pyshorteners` library to shorten the link with the TinyURL service, and prints both the original and shortened links.

2. `link_opener(link)`: This function takes a shortened URL as input, uses the `urlopen` method from `urllib.request` to open the shortened URL, retrieves the real (original) URL, and prints both the shortened and real links.

The script then prompts the user to choose between two options:
- Type `1` to shorten a link.
- Type `2` to extract the real link from a shortened link.

The user inputs their choice and the link they wish to process. Based on the user's input, either the `link_shortener` function or the `link_opener` function is called.

## Complexity and efficiency

The `link_shortener` function leverages the `pyshorteners` library to interface with the TinyURL service, which handles the actual shortening process. The time complexity is O(1) for the function call itself, while the network request to TinyURL's service might vary.

The `link_opener` function uses `urlopen` to open the shortened link and retrieve the original URL. The time complexity is O(1) for the function call, but similar to the shortening process, the network request time may vary.

Both functions have a space complexity of O(1) as they store only a few variables and the results of the URL operations.

Overall, the script provides efficient methods to shorten and expand URLs with minimal computational overhead.