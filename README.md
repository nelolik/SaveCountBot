# SaveCountBot

This bot allows you to save and accumulate the number of repetitions of various types of exercises or statistics such as
the number of kilometers traveled, repetitions of mantras, calories eaten, etc. To use it, you need to create a named 
entry and add the current changes to it. For example, you create a "running" entry and each time you run, you add how 
many kilometers you ran. As a result, you can get how many kilometers you ran in total during trainings.

The bot uses **webhooks** to receive updates from Telegram, providing instant message delivery and better scalability
for production environments.

## Requirements

- Java 17+
- Maven
- Public HTTPS URL with valid SSL certificate (for production)
- Reverse proxy (nginx/apache) configured for HTTPS

## Launching

### Build the application

```shell
mvn clean package
```

### Run the application

Execute the script to run the application:

```shell
java -jar target/SaveCountBot-0.1-SNAPSHOT.jar --bot.name=bot_name --bot.token=token
```

- `bot_name` - registered bot name
- `token` - bot API access token

The bot name and token can also be specified in the `application.properties` file using the `bot.name` and `bot.token` 
parameters. Then you do not need to specify them on the command line:

```shell
java -jar target/SaveCountBot-0.1-SNAPSHOT.jar
```

### Webhook Configuration

The webhook URL is configured in `application.properties`:

```properties
bot.webhook.url=https://bwdnelolik.ru/saveCountBot/callback/update
bot.webhook.path=/callback/update
server.servlet.context-path=/saveCountBot
```

The application will automatically register the webhook with Telegram on startup.

### Verify Webhook Status

Check webhook status using Telegram API:

```shell
curl "https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getWebhookInfo"
```

## Available commands

+ `/list_of_records` - prints a list of records and the number stored.
+ `/new_record` - add a new record type
+ `/new_count` - add a quantity to an existing record
+ `/delete_record` - delete existing record
+ `/hello` - displays a welcome message
