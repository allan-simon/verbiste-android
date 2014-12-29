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
 * you have added the directory containing the android SDK binaries
in your `PATH` variable, for me it was for example
`~/Downloads/android-sdk-linux/tools` and
`~/Downloads/android-sdk-linux/platform-tools`

##Vim configuration

##Creating the project

```
android create project \
    --target 1 \
    --name VerbisteAndroid
    --path . \
    --activity MainActivity \
    --package com.allansimon.verbisteandroid
```

##Compiling

from the command line ``ant debug```

##Pushing the apk to a device

just plug in USB your device (be sure to have activated developer options) and then

```adb install -r bin/VerbisteAndroid-debug.apk```
(`-r` is to indicate we want to reinstall)

##Running unit test

##Check style

#Thanks

thanks to the verbiste project for collecting all the French verbs
and their conjugation
