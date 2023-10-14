<img width="150" height="150" style="float: left; margin: 0 10px 0 0;" alt="MineTinker logo" src="https://i.imgur.com/8ZFiViM.png">

# MineTinker
<p style="text-align: center">
    <a href="https://github.com/Flo56958/MineTinker">
        <img src="https://img.shields.io/github/stars/Flo56958/MineTinker?color=0088ff" alt="Stars"/></a>
    <a href="https://github.com/Flo56958/MineTinker/fork">
        <img src="https://img.shields.io/github/forks/Flo56958/MineTinker?color=0088ff" alt="fork"/></a>
    <a href="https://github.com/Flo56958/MineTinker/graphs/contributors">
        <img src="https://img.shields.io/github/contributors/Flo56958/MineTinker" alt="Contributors"/></a>
    <a href="https://github.com/Flo56958/MineTinker/pulse">
        <img src="https://img.shields.io/github/commit-activity/m/Flo56958/MineTinker" alt="Activity"/></a>
    <a href="https://github.com/Flo56958/MineTinker/issues">
        <img src="https://img.shields.io/github/issues/Flo56958/MineTinker" alt="Issues"/></a>
    <a href="https://www.codefactor.io/repository/github/flo56958/minetinker">
        <img src="https://www.codefactor.io/repository/github/flo56958/minetinker/badge" alt="CodeFactor"/></a>
    <a href="https://discord.gg/ZEVNKhN">
        <img src="https://img.shields.io/discord/493806232784732181?logo=discord"
            alt="chat on Discord"></a>
</p>

A Tinker's Construct inspired Spigot plugin. It currently has over 40 modifiers with more planned. 
Almost everything is configurable: recipes, level caps, which modifiers are enabled, etc.
***

**This plugin is 'balanced' with PvE in Mind. If you want to use it for PvP you need to balance it yourself through the various configurations.**
***
## Development
This repository is maintained by Flo56958 and mainly contributed by [Draycia](https://github.com/Draycia) and Flo56958. 
Everything is made in our free time, and we are getting no money for making this plugin. It is mostly because we enjoy 
coding and having fun making this Plugin. The development of MineTinker is therefore as fast as we see fit and have the 
necessary time and motivation implement new features and fix bugs. The release schedule is not fixed and depends on the 
features and bugfixes planned for the next release which can vary greatly with further development. If you cannot wait on
a feature you want to add to MineTinker you are more than welcome to add the feature yourself and send us a pull request.
If you need support on a code topic, we are glad to assist! (But please do not annoy us with questions on release schedules and how long until a feature is implemented.)
***

## How to contribute to this repository:
MineTinker is in development, and we are constantly fixing bugs and adding new features. 
**Everyone is welcome to contribute to this plugin.**

There are several options for contributing:

- Making suggestions and creative ideas as a feature request with a GitHub-issue or on [Discord](https://discord.gg/ZEVNKhN)</br>
  (An issue on GitHub is for us more easily to manage as it creates a new discussion and is therefore more organized than the discord channel)
- Reporting bugs via [Issues](https://github.com/Flo56958/MineTinker/issues) on GitHub (or less preferably on Discord)
- Forking this repository and adding your own features and bug fixes (Feel free to send a pull request or get in touch 
  with us if you have any questions or problems)</br>
  **Note:** _Every pull request and code change will be carefully examined and may not be accepted without further code changes from our side or requests to the creator of the pull request_
- If you can speak a language other than english you can help us translate MineTinker to different languages. For easy
  translation overview go to: [Transifex](https://www.transifex.com/flo56958/minetinker/dashboard/)
***

## How to configure MineTinker on a server:
MineTinker has multiple config files, each containing other options and settings. 
The [main config](https://github.com/Flo56958/MineTinker/blob/master/src/main/resources/config.yml) contains the core
settings for the plugin. Each modifier has its own config file saved in the 'Modifiers'-folder. These settings are unique
to the modifier. You can access the ingame-Configurations-Editor via the command ```/mt editconfig```.

All config files (except the main one) are generated by Java code when the plugin is first run. Therefore, the repository
does not contain those files directly. The explanation of these settings is next to the Java code for the specific option 
(for the [modifiers](https://github.com/Flo56958/MineTinker/tree/master/src/main/java/de/flo56958/minetinker/modifiers)
this is in the reload()-method)

## Incompatible Plugins:
MineTinker may and will be incompatible to certain plugins.</br> 
Incompatibilities may most likely occur with plugins:

- that rely on **Item-Lore** (as MineTinker will overwrite Lore on some Items like Tools and Armor) </br>
  [Setting ```EnableLore``` to false in ```config.yml``` can improve compatibility as well as using the PatternMatcher in ```layout.yml```]
- that add own **custom enchants**

Some incompatibilities can be negated by tweaking MineTinkers configuration files.

If you found incompatible Plugins that are not known to MineTinker, please consider creating an 
[Issue](https://github.com/Flo56958/MineTinker/issues) or contact us on [Discord](https://discord.gg/ZEVNKhN).