# SaveCountBot

This bot allows you to save and accumulate the number of repetitions of various types of exercises or statistics such as
the number of kilometers traveled, repetitions of mantras, calories eaten, etc. To use it, you need to create a named 
entry and add the current changes to it. For example, you create a "running" entry and each time you run, you add how 
many kilometers you ran. As a result, you can get how many kilometers you ran in total during trainings.

The connection to the host is made using long polling, so the bot can be launched from the local network and
without a static IP address.

## Launching

Execute the script to run the application
```shell
java -jar SaveCountBot-0.1-SNAPSHOT.jar --bot.name=bot_name --bot.toke=token
```
bot_name - registered bot name

token - bot API access token

The bot name and token can also be specified in the application.properties file using the bot.name and bot.token 
parameters. Then you do not need to specify them on the command line. Execute the script to run
```shell
java -jar SaveCountBot-0.1-SNAPSHOT.jar
```
## Available commands

+ /list_of_records - prints a list of records and the number stored.
+ /new_record - add a new record type
+ /new_count - add a quantity to an existing record
+ /delete_record - delete existing record
+ /hello - displays a welcome message

