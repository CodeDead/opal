# Opal

![Opal](https://i.imgur.com/R987DH7.png)

![GitHub](https://img.shields.io/badge/language-Java-green)
![GitHub](https://img.shields.io/github/license/CodeDead/opal)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/CodeDead/opal)

Opal is a free and open-source [JavaFX](https://openjfx.io/) application that can play relaxing music in the background.

## Building

### Windows

#### Installer

You can create an executable installer by running the `jpackage` Gradle task on a Windows host:
```shell
./gradlew jpackage
```
*Do note that you will need the [WiX Toolset](https://wixtoolset.org/) in order to create MSI packages.*

#### Portable image

You can create a portable image by running the `jpackageImage` Gradle task on a Windows host:
```shell
./gradlew jpackageImage
```

### Linux

#### rpm

You can create an RPM, by running the `jpackage` Gradle task on a Linux host:
```shell
./gradlew jpackage
```
*Do note that you will need the `rpm-build` package in order to create an RPM.*

#### AppImage

You can create an [AppImage](https://appimage.github.io/) by executing the `AppImage` Gradle task on a Linux host:
```shell
./gradlew AppImage
```
*Do note that running this task will execute a shell script in order to download and run the [appimagetool](https://appimage.github.io/appimagetool/) in order to create the AppImage file.*

#### Portable image

You can create a portable image by running the `jpackageImage` Gradle task on a Linux host:
```shell
./gradlew jpackageImage
```

## Dependencies

A couple of dependencies are required in order to build Opal. Some of which require a specific host machine,
especially if you're targeting a specific platform and installation method.

The following dependencies are universal:

* [JDK18](https://openjdk.java.net/projects/jdk/18/)
* [JavaFX](https://openjfx.io)
* [Gradle](https://gradle.org)
* [FasterXML/jackson](https://github.com/FasterXML/jackson)
* [badass-jlink-plugin](https://github.com/beryx/badass-jlink-plugin)
* [Log4j2](https://logging.apache.org/log4j/2.x/)
* [AtlantaFX](https://github.com/mkpaz/atlantafx)
### Windows

#### MSI
In order to build an installer (*MSI*) of Opal on Windows, you will need:

* [WiX Toolset](https://wixtoolset.org)

### Linux

#### rpm

In order to create an [RPM](https://en.wikipedia.org/wiki/RPM_Package_Manager), you will need to have the following package(s) installed:

* rpm-build (`dnf install rpm-build`)

## Credits

### Audio

All audio files have been provided by [ZapSplat](https://zapsplat.com).

### Images

All images have been provided by [Remix Icon](https://remixicon.com/).

## About

This library is maintained by CodeDead. You can find more about us using the following links:
* [Website](https://codedead.com)
* [Twitter](https://twitter.com/C0DEDEAD)
* [Facebook](https://facebook.com/deadlinecodedead)

Copyright © 2022 CodeDead
