# MikesTCAddons
Mike's TC Addons: Addons for TrainCarts!

## Installation
1. Install [BKCommonLib](https://www.spigotmc.org/resources/bkcommonlib.39590/) and [TrainCarts](https://www.spigotmc.org/resources/traincarts.39592/)
2. Download this plugin from the Releases page
3. You're good to go!

## Driving Trains
1. Make yourself the owner of the train
2. Run `/throttle on`
3. Drive away!

## Swapping door sides
1. Make two animations with TrainCarts attachments called `door_L` and `door_R`.
2. By placing and activating a `swap` sign underneath, you can swap the `door_L` and `door_R` animations.
3. Alternatively, run `/swap`.

## Opening doors (shorthand method)
1. Make two animations with TrainCarts attachments called `door_L` and `door_R`.
2. To play the `door_L` animation forwards, run `/door l o`.
3. To play the `door_L` animation in reverse, run `/door l c`.
4. To play the `door_R` animation, replace `l` with `r`.

## Decoupling carriages
1. Run `/decouple <no. of carriages>`. If the number is positive, the carriages will be decoupled from the front of the train. Otherwise, they will be decoupled from the back. A value of 0 does nothing.

## Per-minute trigger sign
The per-minute trigger sign does the same as TrainCarts' original [trigger sign](https://wiki.traincarts.net/p/TrainCarts/Signs/Trigger), but it only updates every minute. Use this if the original trigger sign poses a lag issue.
```
[train]
trigger
<variable name>
<time (in minutes)
```
Unlike TrainCarts' trigger sign, this sign **does not** come with a record function.

## Swap sign
This sign swaps the `door_L` and `door_R` animations.
```
[train]
swap
```

## Barrel sign
This sign does different things based on what it is programmed to do, in the Barrel Programming Language (next section). To work, this sign needs to be placed on a barrel.
```
[train]
barrel
```

## Barrel Programming Language (not implemented yet)
The Barrel Programming Language is a specialised programming language similar to Java that is only used with this plugin. It can't do a lot, but it nonetheless can do stuff.

To run anything in Barrel (the language), write it in a book and quill and place it in a barrel (the block). Then, place a barrel sign on it and drive a train over it.

All statements in Barrel must be ended with a `;`.

### Built-in functions and keywords

-`if () {...}` An if statement. Nothing special here.

-`an(text);` Announces what is in the parentheses to the players in the train.

-`ps(sound, volume, pitch);` Plays a sound to the players in the train.

-`sl(variable, new_text);` Updates a signlink variable with the new text.

