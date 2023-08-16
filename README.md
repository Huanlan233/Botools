# Botools
A simple modification that can spawn carpet fake player(bot) by position, angle and dimension you set.

# Usage
The following commands can be used to add, remove, modify, or retrieve information about the bot.

Each world has a corresponding config file in `config/botools/[world_name].json`

**Add a bot to config:**
```
/botools add <bot_name> [<info>] [<position>] [<angle>] [<dimension>]
``` 

**Remove a bot from config:**
```
/botools remove <bot_name>
```
**Modify a bot's config:**
```
/botools modify <bot_name> <info|position|angle|dimension> <value>
```
**List out all bots:**
```
/botools list
```
**Retrieve detailed info about a bot:**
```
/botools info <bot_name>
```