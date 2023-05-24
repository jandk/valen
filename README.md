# valen

Another one of those "I'm bored, let's see what we can do" projects.

As a big fan of Doom Eternal, I wanted to see if I could read a couple of files here and there, just to see what's up.
It appears there's already a couple of tools like that, but I wanted to do it for myself.

## What does it do?

The goal is to document the file formats that are used in the game, and provide a way to read them.

I'm going for full compatibility. If it can read a file, I mean all files of that type.
With (preferably) no exceptions.

Currently working:

- `image` - Can export to DDS (with all LODs) and PNG.
- `model` - No export implemented yet.

I still have a lot of testing code lying around that I'm cleaning up and can dump in here pretty soon.

## What does it not do?

At this time, quite a lot. This is still a work in progress, but I wanted to get the code out there,
so people can see what I'm doing, and maybe even help out.

Also, there's no UI right now. It's more of a library than a tool.
The only way to make it do anything right now is to write some code.

Which reminds me that I should probably throw in a sample here, or at least a simple main method.

## Compared to Vega

Vega being closed source is what ticked me off enough to just dig in myself. A fun weekend project turned into a
full-blown project, and I'm not even sure if I'll ever finish it. But I'm having fun, and that's what matters.

- Vega is closed source, this is not.
- Vega is a GUI, this is not (yet).
- Vega exports the wrong textures, this creates a full DDS with all LODs.
- Vega exports some models incorrectly, this exports them correctly.

## Credits

- SamPT (@brongo) - He did most of the leg work, and his work is the basis for this project.
- [Doom Eternal Modding Discord](https://discord.gg/6yvZs2U) - For being a great community.
