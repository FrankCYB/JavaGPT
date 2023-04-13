
# JavaGPT

A Java GUI that interfaces ChatGPT API.


![](https://i.imgur.com/lI8oufi.gif)


## Features

- Proxy support
- Import premade prompts
- Save chats to file
- Cross platform


## Setup

To get started download the [latest release](https://github.com/FrankCYB/JavaGPT/releases/latest "Latest release page").

Afterwords, extract the archieve

Then open the config.properties file in a text editor

Add your [ChatGPT API-Key](https://platform.openai.com/account/api-keys "ChatGPT API-Key") on the first line after "apikey="

Run JavaGPT.jar and enjoy! 😁



## Config Example
```
apikey=ENTERAPIKEYHERE

proxyip= //Enter proxy here
proxyport= //Enter proxy port number here

OkHttpClient=true // Disable or Enable OkHttpClient
OHC_connectTimeout=10 
OHC_writeTimeout=10 
OHC_readTimeout=30 // Adjust allowed wait time for prompt response from ChatGPT API

WindowSize=default //Options: small,default
```
## Requirements

- Java 8 or higher
    
## Final notes

Shoutout to LiLittleCat for making a [ChatGPT API library](https://github.com/LiLittleCat/ChatGPT "Latest release page") for Java

Made my life much easier 😁👍


