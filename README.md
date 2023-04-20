
<div>
    <img src="https://i.imgur.com/Lv2dBHU.png" alt="Image description" style="display: inline-block; width: 80px; height: 80px;">
</div>

# JavaGPT

A Java GUI that interfaces ChatGPT API.


![](https://i.imgur.com/EbL1VRv.gif)


## Features

- Chat Streaming
	- Just like the website, responses will generate in real time
	- You can terminate a response while it is in progress
- Chat History
	- See and interact with previous chats
	- Saves chats as .json for easy external modification and viewing
	- Accessible through the "Load Chat" button

![Demo](https://i.imgur.com/q3s1frY.gif)

- Chat Titles
	- Autogenerate titles like ChatGPT website
	- Manually name chats if preferred
- Revert Chats
	- Be able to void previous prompts and responses from chat
	- You can revert multiple times
- HTML Viewer
	- View your chat content in HTML
	- Supports Markdown Language syntax

![HTML View](https://i.imgur.com/W0pzIic.gif)


- Import premade prompts
- Save chats to file
- Support for ChatGPT 4, and 3.5 models
- Cross platform


## Setup

To get started download the [latest release](https://github.com/FrankCYB/JavaGPT/releases/latest "Latest release page").

Afterwords, extract the archieve

Then open the config.properties file in a text editor

Add your [ChatGPT API-Key](https://platform.openai.com/account/api-keys "ChatGPT API-Key") on line 4 after "apikey="

Run JavaGPT.jar and enjoy! 😁



## Config Example
```
apikey=ENTER_CHAT_GPT_API_KEY_HERE
model=gpt-3.5-turbo		#Model used for ChatGPT Client (Supported Models: gpt-4, gpt-3.5-turbo, etc) > All supported models here "https://platform.openai.com/docs/models/gpt-3-5"
maxTokens=1024			#Max ammount of tokens allowed per ChatGPT API request
timeout=30			#Adjust allowed wait time for prompt response from ChatGPT API
autosave=true			#Adjust wether chats will save automatically (Necessary for chat history to work properly)
autotitle=false			#Adjusts wether new chats will automatically generate file name titles based on the context of the chat
chat_location_override=		#Overrides default "chat_history" folder path (Original path is set to the location of the jar file on runtime)
WindowSize=			#Adjusts JFrame (Window) size. Options: small,medium,large (Set to "medium" by default)
Theme=dark			#Themes JFrame (Window) to set config. Options: dark,light
```
## Requirements

- Java 8 or higher
    
## Final notes

If you enjoy JavaGPT and would like to support me in future updates and projects, please feel free to show your support by buying me a ☕ 

<a href="https://www.buymeacoffee.com/FrankCYB" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>




Also, shoutout to TheoKanning and his contributors for making [OpenAI-Java](https://github.com/TheoKanning/openai-java "Project page") : A ChatGPT API wrapper for Java

Made my life much easier 😁👍


