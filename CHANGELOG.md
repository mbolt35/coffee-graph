# Coffee-Graph Change Log

## 0.1.0

### Node Script Improvements
* Added a java runtime check to node.js script.
* Modularize `coffee-graph` node.js execution script by splitting common functionality for pathing into `PathHelper` and Java centric functionality to `JavaHelper`.
* Update node version requirement, and pass back the path search for windows testing.

### Coffee-Graph "Options" Refactor
* Created a more generic options parser and push all JCommander code to a specific implementation.
* Added a default exporter factory and updated implementation in CoffeeGraph main. 
* The `CoffeeGraph` class now accepts a `CoffeeGraphOptionsParser` implementation and `ExporterFactory` implementation, opening the door for inclusion in external modules or build plugins.


## 0.0.8

### Improvements
* Instead of compiling each `.coffee` file one at a time and appending, concatenate all the ordered `.coffee` files, then pass the result to the CoffeeScript compiler.
* Update the compiler to accept the `--bare` option.
* Added `--bare` and `-b` usage descriptions to command-line output.
* Update the dependency resolution such that assignment of a variable to `this` will retain the `this` rules.

### Polish and Bug Fixes
* Add two new unit tests for new functionality.
* (mhart) Corrected compilation information in usage: CoffeeScript should be JavaScript.


## 0.0.7 (Hotfix)
* Fix another issue with windows pathing, and bump to 0.0.6
* Added a formatted usage and updated some of the descriptions.


## 0.0.6 (First Public Release)

### General Polish and Bug Fixing
* Added a "pseudo-tree" dependency printer to the GraphUtils. 
* Added a better looking stylesheet for javadocs, and updated the maven-javadoc-plugin
* Added `--help` and `-h` option to the commands list, which prints the usage information.
* Added `--version` and `-v` option to the commands list, which prints the Coffee-Graph, CoffeeScript, and Java versions.
* Added the license file, update the build to generate a jar with dependencies.
* Link in the application version value from the build using a groovy script and gmaven plugin. Write out the coffee-graph header in compilation.
* Added package.json for NPM package, updated build output location, and added a node script for executing the jar.
* Updated the coffee-graph node.js binary to be more windows friendly. This is a side effect of passing command line arguments from node -> java
* Fix an issue where the files returned weren't windows compatible.


## 0.0.1 - 0.0.5

### Initial Project Creation
* Unit tests added and verified as working.
* Added Maven build configuration.
* Added a README which outlines an initial road map.
* Updated License information in source files and updated gitignore with project files.

### Bug Fixes and New Features
* Added multiple use-cases for testing.
* Added a dependency builder for "pluggable" interface. 
* Added an `Exporter` interface which defines how the dependencies are handled after they have been analyzed.
* Added a few default exporters.
* Fixed and issue where function references were not being used. 
* Fixed an issue where re-assigning a global identifier before deaming it "override." 
* Fixed the copy method on DependencyGraph to actually copy the map contents.

### Preparing for Release
* Added lots of new features to the exporters that wire into the command-line options.
* Added new exceptions and better exception handling in order to give proper feedback.
* Switched all identifier lists to sets in the dependencies - will cut down on the iteration over duplicate identifiers. 
