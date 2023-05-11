# PL0C

Small and (relatively) simple compiler for the PL/0 programming language written
in Kotlin, targeting the own, custom built stack machine.

The source language, while still called PL/0 isn't actually fully compliant with
the original PL/0 by Niklaus Wirth, is loosely based on it and more a mix of 
PL/0 and SimpleC.

## How to build and run?
This project uses Gradle as a build system, so to build the software, use the
following commands:

On Linux/macOS:
```
./gradlew clean build shadowJar
```

On Windows:
```
./gradlew.bat clean build shadowJar
```

This builds the program and assembles a jar file that includes all relevant
dependencies (currently there are none). It is located in the following folder:
```
build/libs/
```

To run, execute the following command:
```
java -jar <jarfile>
```

The compiler reads the source code from STDIN, and directly executes the 
compiled program (or throws an error if you programmed something weird...)
