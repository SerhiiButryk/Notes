Kotlin Multiplatform Notes application

Cloning with submodules:

$ git clone /url/to/repo/with/submodules
$ git submodule init
$ git submodule update

Add new submodule:

$ git submodule add https://bitbucket.org/jaredw/awesomelibrary

Declare IOS shared lib:

listOf(
iosX64(),
iosArm64(),
iosSimulatorArm64(),
).forEach { iosTarget ->
iosTarget.binaries.framework {
baseName = "Shared"
isStatic = true
}
}

The ongoing TODO list:

1. Investigate Android proguard settings:

kotlin { androidLibrary { ...
   optimization {
   // TODO: Might need to set proguard rules here
   }
...

2. Enable kotlin android lint 

The ongoing improvements/features:

1. Add html to pdf export feature

- Need to use WebView or native canvas drawing. No other approaches so far.

2. Add back button on Editor UI screen

3. Adjust colors issues in Dark mode

4. Investigate memory state during runtime


