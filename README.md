[![Build Status](https://github.com/SerhiiButryk/Notes/actions/workflows/ci.yml/badge.svg)](https://github.com/SerhiiButryk/Notes/actions)

# Notes application 💜💜💜

<img src="images/icons/Dark/Kotlin Multiplatform Logo.png">

# What is it ? ✨

Cross-platform, secure, fluent and rich editor for your notes, which is built with KMP.

# Current state

Under active development.

# Design

<img src="images/package_structure.png">

**Cloning the repo with submodules**

```
$ git clone git@github.com:SerhiiButryk/Notes.git
$ git submodule init
$ git submodule update
```

**The ongoing improvements/features**

1. Investigate memory issues during runtime

2. Test with large notes > 1 GB

3. Implement UI improvements (Search, Labels ...)

4. Can add PlayIntgrity check using Firebase. However, it needs Google Play console configuration which is not possible now.
