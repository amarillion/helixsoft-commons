Zipper tool
====

Overview
---

Zipper is a tool that creates zip / tar.gz bundles according to instructions
from an ini-style configuration file.

Zipper lets you:
* define the name of the top-level directory inside the archive
* select files to include using glob patterns
* create multiple bundles from a single config file
* put zip entries in a different relative location
* generate precisely the same tar.gz and zip bundles
* use macro variables using C-style #include and #define

Configuration files
---

Zipper configuration files are somewhat similar to ini style, but there
are differences. There are four types of lines in a configuration file.

1. Section headers. A section header is a name between brackets such as `[windows]`.
   This name can be selected from the command line.

2. Properties of the form `property=value`

3. Comments starting with a #. Example: '# this is a comment'

4. Include lines. Include a file containing C-style #defines.
   Example: `#include 'version.inc'`

5. Filename selection, in the form of a glob. The selected files
   will be mapped within the archive to the directory defined by the property
   basedir. Example: `src/*.cpp`

6. Filename mapping. A mapping consists of two parts, a file selection followed
   by `->` followed by a directory location. The selected files will be included
   as well, but will be mapped to a different directory than the basedir.
   Example: `build/release_win/GAME.exe -> top`

Each section corresponds to a selection of files, which will be bundled
into a zip or tar archive. The generated file will look like
basename + version + section suffix + tar.gz / .zip

The main section can have the following properties:

* basename:
    Defines the directory, and prefix of the archive filename.
* version:
    Defines the middle part of the archive filename
* basedir:
    Defines the default top-level directory within the archive

Per-section properties:

* suffix:
    Section-specific suffix that is appended at the end of the arvhive filename,
    before the filename extension

Here is a complete sample configuration file, containing two sections.
One named 'src' for generating a source bundle. One named 'win' for a bundle
containing the windows binary.

```
#include 'version.inc'
# version.inc defines the value for APPLICATION_SHORT_VERSION
version=${APPLICATION_SHORT_VERSION}
basename=dist/GAME
basedir=top

[src]
suffix=src
src/*.cpp
include/*.h
data/*.*
makefile
icon.ico
icon.rc
LICENSE.txt
README.txt
bun.sh
# The following two entries will be mapped to
# a separate zip directory outside the root 'top'
# In effect this zip archive will contain two top-level
# directories.
../twist5/src/*.cpp -> twist5/src
../twist5/include/*.h -> twist5/include

[win]
suffix=win
README.txt
LICENSE.txt
data/*.*
build/release_win/GAME.exe -> top
build/release_win/*.dll -> top
```

Usage
---

By default, zipper will look for a configuration named zipper.conf in
your working directory. You can override this default iwth the -c option.

Command-line arguments:
```
zipper [VAL] [options]

   VAL               : run selected sections only
   --format (-f) VAL : which formats to produce. Valid values: tgz, zip or both.
                       Default: both
   --help (-h)       : Show usage
   --version (-v)    : Print version and quit
   -c FILE           : Configuration file
```

Build instructions
---

Zipper uses the java build-tool gradle. Run the command

```
cd nl.helixsoft.zipper
gradle jar
```

This will create an executable jar file in `build/libs/zipper-xxx.jar`
