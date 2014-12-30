#Verbiste android 

An open-source android application to get the conjugation of French verbs
and to get back a verb from its conjugated form

#License

The database is licensed under GPLv2 as required by verbiste project

#How the project has been created from command line and vim

first of all a lot of the command line can be found in the actual
[android training guide](https://developer.android.com/training/index.html),
so if a commands seems outdated, refers to that training guide

##Requirements

 * you have already downloaded the last android SDK
 * you have downloaded the Android Support Library
 * you have added the directory containing the android SDK binaries
 * you're using Pathogen to handle your vim plugins
in your `PATH` variable, for me it was for example
`~/Downloads/android-sdk-linux/tools` and
`~/Downloads/android-sdk-linux/platform-tools`

 * you've added these environment variables `ANDROID_HOME` and `ANDROID_SDK`
both pointing to the path of your SDK

##Vim configuration

    * git clone https://github.com/vim-scripts/javacomplete
    * git clone https://github.com/hsanson/vim-android
    * git clone https://github.com/bpowell/vim-android

the two vim-android are actually slightly different, also for
`bpowell/vim-android` I did edit the installation script to not compile
supertab and the "textmate" plugin and I needed to install exuberant-ctags

##Creating the project

```
android create project \
    --target 1 \
    --name VerbisteAndroid
    --path . \
    --activity MainActivity \
    --package com.allansimon.verbisteandroid
```

then add the link to appcompat v7 (Note, it **MUST** be a relative path)

```
android update project \
    --target 1 \
    --path . \
    --library ../path/to/android-sdk-linux/extras/android/support/v7/appcompat
```

you also need to generate a build.xml for that library, go in the
`extras/android/support/v7/appcompat`

and run

```
android update project --target 1 -p .
```


##Compiling

from the command line ``ant debug```

##Pushing the apk to a device

just plug in USB your device (be sure to have activated developer options) and then

```adb install -r bin/VerbisteAndroid-debug.apk```
(`-r` is to indicate we want to reinstall)

##Running app in debug mode

simply run the script ```./utils/debug.sh .```
(see the file to if you need to understand the magic)

##Running unit test

##Check style

#Thanks

thanks to the verbiste project for collecting all the French verbs
and their conjugation
