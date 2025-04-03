# Bell Choir
### Table of Contents

1. [Overview](#overview)
2. [Built with](#built-with)
3. [Prerequisites (Sort of)](#prerequisites-sort-of)
   - [Required JUnit Dependencies](#required-junit-dependencies)
4. [Getting Started](#getting-started)
   - [Clone the Repository](#clone-the-repository)
5. [Running the Program](#running-the-program)
   - [Build and Run with Ant](#build-and-run-with-ant)
6. [Testing](#testing)


## Overview

Bell Choir is a Java application that simulates a multi-threaded bell choir where individual threads ([Members](src/main/Member.java)) each play their own [BellNote](src/main/sound/BellNote.java). The [Conductor](src/main/Conductor.java) class orchestrates the choir by coordinating when each member should play their notes.

The project demonstrates key multi-threading concepts:
- Thread synchronization for musical timing
- Dynamic thread management as choir members are assigned notes
- Real-time audio synthesis for bell sounds
- Error handling for invalid notes and timeout conditions

Songs are loaded from text files in the [data](data/) directory, which contain sequences of notes and their durations. The [SongReader](src/main/SongReader.java) parses these files and converts them into playable note sequences.

Each Member thread waits for its turn to play, then produces its assigned note with the correct pitch and duration. The Conductor ensures that all notes play in the proper sequence, creating a synchronized musical performance.

The project was built using Java and Apache Ant, with JUnit tests to verify functionality of key components like the SongReader.


## Built with
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Apache Ant](https://img.shields.io/badge/Apache%20Ant-A81C7D?style=for-the-badge&logo=apache&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-5.8.1-green?logo=junit5&style=for-the-badge)

## Prerequisites (Sort of)
To test the [SongReader](src/main/SongReader.java) class, I utilized [Junit 5](https://junit.org/junit5/). To be able to run the tests and compile the java classes, the required JUnit dependencies need to be downloaded in the lib folder.  

### Required JUnit Dependencies

- **JUnit Jupiter API (5.8.1)**  
  [junit-jupiter-api-5.8.1.jar](https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.8.1/junit-jupiter-api-5.8.1.jar)  

- **JUnit Jupiter Engine (5.8.1)**  
  [junit-jupiter-engine-5.8.1.jar](https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.8.1/junit-jupiter-engine-5.8.1.jar)  

- **JUnit Platform Engine (1.8.1)**  
  [junit-platform-engine-1.8.1.jar](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-engine/1.8.1/junit-platform-engine-1.8.1.jar)  

- **JUnit Platform Launcher (1.8.1)**  
  [junit-platform-launcher-1.8.1.jar](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-launcher/1.8.1/junit-platform-launcher-1.8.1.jar)  

- **JUnit Platform Commons (1.8.1)**  
  [junit-platform-commons-1.8.1.jar](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-commons/1.8.1/junit-platform-commons-1.8.1.jar)  

- **OpenTest4J (1.3.0) - Assertions & Error Reporting**  
  [opentest4j-1.3.0.jar](https://repo1.maven.org/maven2/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar)  

- **API Guardian (1.1.2) - API Status Annotations**  
  [apiguardian-api-1.1.2.jar](https://repo1.maven.org/maven2/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar)  

**NOTE**: This project is not tested with other versions of each dependency. I believe it will work with most version of JUnit 5, but I'm 99% sure it will not work with JUnit 4.

To download the required dependencies, you can:
1. Navigate to the repository
```bash
cd /filePath/BellChoir
```
2. Download
```bash
ant download-junit
```
This will download the dependencies in a new folder titled `lib`. The project should compile successfully after the dependencies are downloaded successfully.  
To remove the `lib` folder and it's contents, run
```bash
ant remove-libs
```

**NOTE**: Should you choose not to download the JUnit dependencies, they will be downloaded for you automatically before the project is compiled.

## Getting started

* Open a terminal
* If Ant isn't installed and you have a mac with homebrew, run
```bash
brew install ant
```
If you don't have a mac with homebrew, sorry.
### Clone the repository
```bash
git clone https://github.com/CanFam23/BellChoir/
```

## Running the program
The program utilizes command line arguments to determine what file to load a song from. I'm sure there is a way to run the program through an IDE with C.L.A., but my instructions will only cover running through the command line.

The program takes one argument, `song`, which is the name of the file to read to get the notes to play. All files you would like to play should be placed in the [data](data/) folder, as that is the where the program checks to load all files from. I have provided several files to play, which are:
- **[MaryLamb.txt](data/MaryLamb.txt)**: The classic, Mary had a Little Lamb
- **[RickRolled.txt](data/RickRolled.txt)**: Self explanatory ([hopefully](https://www.youtube.com/watch?v=dQw4w9WgXcQ))
- **[InvalidMusic.txt](data/InvalidMusic.txt)**: Contains mostly invalid notes, mainly used for testing.
- **[EmptyFile.txt](data/EmptyFile.txt)**: A empty file.
- **[Text.txt](data/Text.txt)**: A text file with random text, also used for testing.


### Build and Run with Ant
1. Navigate to the repository
```bash
cd /filePath/BellChoir
```
2. Run with Ant
Run the program using
```bash
ant run -Dsong=Your_Song.txt # Replace 'Your_Song.txt' with the song you want to play
```
Other helpful commands:
```bash
ant clean # Deletes old compiled files.
ant compile # Compiles Java source files into bin/
ant download-junit # Downloads JUnit dependencies in a new folder called 'lib'
ant remove-libs # Remove the 'lib' folder and its contents
ant test # Run all of the unit tests.
```

#### Sample output of `ant run -Dsong=MaryLamb.txt`:
```bash
$ ant run -Dsong=MaryLamb.txt
Buildfile: /path/to/project$/BellChoir/build.xml
Trying to override old definition of task javac

init:

download-junit:
    [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-jupiter-api-5.8.1.jar
    [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-jupiter-engine-5.8.1.jar
    [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-platform-engine-1.8.1.jar
    [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-platform-launcher-1.8.1.jar
    [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-platform-commons-1.8.1.jar
    [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/opentest4j-1.3.0.jar
    [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/apiguardian-api-1.1.2.jar

compile:
    [javac] Compiling 1 source file to /path/to/project/BellChoir/dist/classes

jar:
    [jar] Building jar: /path/to/project/BellChoir/dist/BellChoir.jar

songarg:

run:
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 1 plays: BellNote{note=G4, length=QUARTER}
    [java] Member 2 plays: BellNote{note=F4, length=QUARTER}
    [java] Member 1 plays: BellNote{note=G4, length=QUARTER}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 3 plays: BellNote{note=A5, length=HALF}
    [java] Member 1 plays: BellNote{note=G4, length=QUARTER}
    [java] Member 1 plays: BellNote{note=G4, length=QUARTER}
    [java] Member 4 plays: BellNote{note=G4, length=HALF}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 3 plays: BellNote{note=A5, length=HALF}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 1 plays: BellNote{note=G4, length=QUARTER}
    [java] Member 2 plays: BellNote{note=F4, length=QUARTER}
    [java] Member 1 plays: BellNote{note=G4, length=QUARTER}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 1 plays: BellNote{note=G4, length=QUARTER}
    [java] Member 1 plays: BellNote{note=G4, length=QUARTER}
    [java] Member 0 plays: BellNote{note=A5, length=QUARTER}
    [java] Member 1 plays: BellNote{note=G4, length=QUARTER}
    [java] Member 5 plays: BellNote{note=F4, length=WHOLE}

BUILD SUCCESSFUL
Total time: 10 seconds
```

## Testing
As mentioned earlier, I used JUnit to test my code. To run the tests, run
```bash
ant test
```
and you should get an output like

```bash
$ ant test
Buildfile: /path/to/project/BellChoir/build.xml
Trying to override old definition of task javac

init:
    [mkdir] Created dir: /path/to/project/BellChoir/dist
    [mkdir] Created dir: /path/to/project/BellChoir/dist/classes
    [mkdir] Created dir: /path/to/project/BellChoir/dist/test-reports

download-junit:
      [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-jupiter-api-5.8.1.jar
      [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-jupiter-engine-5.8.1.jar
      [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-platform-engine-1.8.1.jar
      [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-platform-launcher-1.8.1.jar
      [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/junit-platform-commons-1.8.1.jar
      [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/opentest4j-1.3.0.jar
      [get] Destination already exists (skipping): /path/to/project/BellChoir/lib/apiguardian-api-1.1.2.jar

compile:
    [javac] Compiling 7 source files to /path/to/project/BellChoir/dist/classes

test:
[junitlauncher] Running test.SongReaderTests
[junitlauncher] Tests run: 27, Failures: 0, Aborted: 0, Skipped: 0, Time elapsed: 0.208 sec

BUILD SUCCESSFUL
Total time: 2 seconds
```

After running, the program will produce a test report, which can be found in the `dist/test-reports` directory. The report contains what tests were run and the Standard Out/Error each test produced. If a test fails, it will also include the cause of the fail in the file. 