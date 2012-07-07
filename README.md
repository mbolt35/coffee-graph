# Coffee-Graph

Coffee-Graph is an automated build tool for CoffeeScript, which is able to analyze large multi-file projects and output an **ordered** listing of the files using cross-file dependencies (if they exist). Coffee-Graph does not require the use of any external JS libraries like RequireJS; just plain vanilla CoffeeScript.


## Installation
Coffee-Graph depends on the [Java 6 runtime](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or greater, so ensure that Java is installed and on your ```$PATH```.

Installation of binaries via npm:

    $ npm install -g coffee-graph
   
Check installation:

    $ coffee-graph -v
    [CoffeeGraph v0.1.0]
     +- Running: CoffeeScript v1.3.3
     +- Using: Java v1.6.0_33 from Apple Inc.


## Usage

    Usage: coffee-graph [options] file.coffee ... [directory]
    Options:
        --bare, -b       Compiles the CoffeeScript source without a security wrapper
        
        --compile, -c    Performs an ordered file join and compilation to JavaScript
                         
        --help, -h       Show the coffee-graph command line usage.
                         
        --print, -p      Prints the ordered .coffee files on a single line.
                         
        --println, -pl   Prints the ordered .coffee files, one per line.
                         
        --tree, -t       Prints a dependency tree.
                         
        --version, -v    Displays the current version of coffee-graph.
                         
        -o               The file to output the dependency graph to.
                         Default: lib/coffee-graph.js

You can always bring up the usage options with:

    $ coffee-graph -h


## Coffee-Graph Demo
Let's say I have a directory `src` that contains three `.coffee` source files:

`src/A.coffee`

    class @A
      constructor: (@name) ->

      doSomething: -> 
        helloWorld "Dookie McGee", 51

`src/B.coffee`

    class @B
      constructor: (@name) ->
      
    this.helloWorld = (name, age) -> 
        console.log "Hello World! My name is #{name} and I am #{age} years old."

`src/C.coffee`

    class AnotherBlah
      constructor: ->

      doIt: ->
        okDoIt = ->
          seriouslyThisTime = ->
            a = new A()
            a.doSomething()
          seriouslyThisTime()
        okDoIt()

    class Blah
      constructor: ->

      doIt: ->
        aBlah = new AnotherBlah()
        aBlah.doIt();

    b = new Blah()
    b.doIt()

Using Coffee-Graph, we can generate the correct file ordering for successful execution in a browser.

### Print

Using the `--print` or `-p` command, we can have Coffee-Graph output the ordered files on a single line:

    $ coffee-graph -p src
    /path/to/src/B.coffee /path/to/src/A.coffee /path/to/src/C.coffee

Similarly, using the `--println` or `-pl` option will output the ordered files, one file per line:

    $ coffee-graph -pl src
    /path/to/src/B.coffee 
    /path/to/src/A.coffee 
    /path/to/src/C.coffee
    
### Dependency Tree

Using the `--tree` or `-t` command will output a tree-like structure. This tree can be used to visualize the tokens Coffee-Graph has tagged as dependencies, and how they are shared across other dependencies. Keep in mind that this show's both incoming and outgoing edges of the dependency graph:

    $ coffee-graph -t src
    
    Coffee-Graph Object Dependency Tree
    ===================================
    [age]
    [helloWorld]
    [A]
     +- [helloWorld]
    [Blah]
     +- [A]
     |   +- [helloWorld]
    [b]
     +- [A]
     |   +- [helloWorld]
    [AnotherBlah]
     +- [A]
     |   +- [helloWorld]
    [B]
    [name]
    [doIt]
     +- [A]
     |   +- [helloWorld]
     ===================================
    

### CoffeeScript Compilation

Using the `--compile` or `-c` command along with the `-o` (output) command will forward the ordered files directly to the CoffeeScript compiler. Currently, Coffee-Graph uses an embedded CoffeeScript compiler (`v1.3.3`). 

The following will output a single `output.js` file containing the ordered compiled `.coffee` files in the `src` directory:

    $ coffee-graph -o output.js -c src
    Compiling CoffeeScript -> /path/to/output.js
    Compiling: B.coffee
    Compiling: A.coffee
    Compiling: C.coffee
    Coffee-Script compiled successfully.
    
    
The contents of the `output.js` file are:

    // [Dependencies Linked with CoffeeGraph v0.1.0]
    // [Compiled with CoffeeScript v1.3.3]

    this.B = (function() {

      function B(name) {
        this.name = name;
      }

      return B;

    })();

    this.helloWorld = function(name, age) {
      return console.log("Hello World! My name is " + name + " and I am " + age + " years old.");
    };


    this.A = (function() {

      function A(name) {
        this.name = name;
      }

      A.prototype.doSomething = function() {
        return helloWorld("Dookie McGee", 51);
      };

      return A;

    })();

    var AnotherBlah, Blah, b;

    AnotherBlah = (function() {

      function AnotherBlah() {}

      AnotherBlah.prototype.doIt = function() {
        var okDoIt;
        okDoIt = function() {
          var seriouslyThisTime;
          seriouslyThisTime = function() {
            var a;
            a = new A();
            return a.doSomething();
          };
          return seriouslyThisTime();
        };
        return okDoIt();
      };

      return AnotherBlah;

    })();

    Blah = (function() {

      function Blah() {}

      Blah.prototype.doIt = function() {
        var aBlah;
        aBlah = new AnotherBlah();
        return aBlah.doIt();
      };

      return Blah;

    })();

    b = new Blah();

    b.doIt();


### Chaining Coffee-Graph Options

You can also chain command line options together:

    $ coffee-graph -pl -t -o output.js -c src
    /Users/mattbolt/Development/coffee-graph/src/test/resources/demo/B.coffee
    /Users/mattbolt/Development/coffee-graph/src/test/resources/demo/A.coffee
    /Users/mattbolt/Development/coffee-graph/src/test/resources/demo/C.coffee

    Coffee-Graph Object Dependency Tree
    ===================================
    [age]
    [helloWorld]
    [A]
     +- [helloWorld]
    [Blah]
     +- [A]
     |   +- [helloWorld]
    [b]
     +- [A]
     |   +- [helloWorld]
    [AnotherBlah]
     +- [A]
     |   +- [helloWorld]
    [B]
    [name]
    [doIt]
     +- [A]
     |   +- [helloWorld]
    ===================================
    Compiling CoffeeScript -> /Users/mattbolt/output.js
    Compiling: B.coffee
    Compiling: A.coffee
    Compiling: C.coffee
    Coffee-Script compiled successfully.

## Usage Notes, Future Updates, and Limitations

### Future Updates

Concerning the embedded CoffeeScript compiler, there are a few improvements we can make here:
* Build a node.js module which can retrieve the file listing to be plugged into a cake build.
* Allow the CoffeeScript compiler JS to be passed in as a URL.


### Limitations

Coffee-Graph isn't perfect, and there will be CoffeeScript syntax that may cause unexpected output. Additionally, the current version of Coffee-Graph bases dependencies on objects attached to the `this` or `@` object in each file scope. 

Initially, this limitation was a concern due to the `exports` / `require` module pattern used in node.js. However, consider the following example:

`Foo.coffee`:
    
    root = exports ? this
    
    root.Foo = class Foo 
        constructor: ->
        
        getName: -> "Foo"
        
`Bar.coffee`:
    
    Foo = require('./Foo').Foo
    
    f = new Foo()
    console.log f.getName()
    
    
In this example, CoffeeGraph will not recognize that it should attach the `this` semantics to `root`, and `Foo` will not be treated as "global." Fortunately, in `Bar.coffee`, `Foo` is defined as `require './Foo'`, so the ordering is irrelevant. In short, if your code uses `exports` and `require` properly, then you most likely won't need Coffee-Graph to link dependencies. 

Based on what I've described in the above example, consider the following:

`Foo.coffee`

    root = this
    
    root.Foo = class Foo 
        constructor: ->
        
        getName: -> "Foo"
        
`Bar.coffee`
    
    f = new Foo()
    console.log f.getName()
    
As of version `0.0.8`, a variable assigned to `this`, `@`, or `window` will inherit the dependency link rules. This solution will most likely not cover all aspects of the CoffeeScript syntax, but hopefully it will provide better support for applications targetting browser and node.js.


## How It Works
Coffee-Graph was built in Java 6 and uses Mozilla's Rhino javascript engine to wrap the [CoffeeScript compiler](https://github.com/jashkenas/coffee-script/blob/master/extras/coffee-script.js). Each `.coffee` source file that is passed to the application is passed through the `CoffeeScript.tokens()` method. The tokens are then added to a pseudo Abstract Syntax Tree (AST), where each level of scope is generated based on `INDENT` and `OUTDENT` tokens. Parsing each file will also generate it's own file-level scope in the tree. 

Using this `CoffeeTree` instance, we can search for globally defined identifiers (using `@`,`this`, or `window`) as well as outgoing and incoming edges between those identifiers. Using this information, Coffee-Graph builds a dependency graph using the outgoing/incoming edges relating the identifiers. 

Once our dependency graph is generated, we can execute a topological sort on the graph, which gives us an ordered list of the identifiers, which can then be mapped back to their originating file. 


## Why?
The popularity of CoffeeScript has caught the eye of a lot of full-stack developers as a **real** attempt to ease the burdens of javascript programming. However, everyone knows the ups and downs of learning a new programming language, and if you add in unfamiliar technology like node.js and npm, coupled with unfamiliar project packaging, deployment, and templates, it becomes overwhelming very quickly. 

The basic concept of Coffee-Graph is this:
> Organize CoffeeScript sources like you would a Java or C# project, in separate folder structures or packages, without worrying about how you're going to reach the compiled product.


## The Story
Full-stack developers have conventions that are difficult to get away from, especially those who fancy C# and/or Java. These conventions are like breathing to us, and when experimenting with new languages, we tend to carry over those conventions as sort of a security blanket. 

CoffeeScript was really my first step into the vastly expanding world of javascript. I have been always been a full-stack programmer, and needed a gateway into the unfamiliar territory of web development. CoffeeScript was that gateway for me; it's elegant, simple, and has just enough OO tendencies to make a full-stack guy feel welcome. 

In May 2012, I started my first CoffeeScript project, and noticed I was focusing on the externals of the project. I couldn't get my code to run correctly in the browser and was constantly flustered at the lack of information provided by the debugger. Soon, I came to realize that a majority of my problems were based on my project setup. In C#, Java, and ActionScript, you don't necessarily have to worry about **where** on the file system a source file exists. The compiler will link all the source together.

I needed these external concerns to go away, so I started to build a maven plugin that did some quick find/replace in the source files to order them. The quick and dirty solution didn't hold up too long, and it was clear I needed a much more intelligent approach for finding dependencies between files. And what better way to learn a language than to dive right into the compiler. 

The original find/replace algorithm turned into an attempt to reproduce the CoffeeScript grammar in `javacc`. I had some success, but ultimately, my grammar was only 90% accurate when parsing the CoffeeScript unit tests. After this approach, I looked into [JCoffeeScript](https://github.com/yeungda/jcoffeescript), which executes the JS CoffeeScript compiler using [Rhino](http://www.mozilla.org/rhino/). Obviously, this was a much more maintainable approach, and the rest is history.


## Building/Running the Source
Coffee-Graph uses [Maven](http://maven.apache.org/) to compile and package the binaries. 

To build: 

    $ mvn clean install

To execute with Node.js:

    $ node ./bin/coffee-graph <options> [files]

To execute with Java:

    $ java -jar bin/coffee-graph <options> [files]

To install from source:

    $ npm install -g .


## Bug Reporting
Please create tickets in the GitHub Issue tracker as you run into them, and I will do my best to address them as soon as possible. 


## About Me
Name: Matt Bolt<br/>
Email: mbolt35@gmail.com<br/>
Twitter: mbolt35<br/>
Web: [mattbolt.blogspot.com](http://mattbolt.blogspot.com)<br/>
Occupation: Lead Game Developer at Electrotank<br/>
Education: B.S. in Computer Science, Clemson University<br/>


## Special Thanks
Thanks to Jeremy Ashkenas and all who have contributed to the CoffeeScript project.<br/>
Thanks to David Yeung and the contributors on the JCoffeeScript project.<br/>
Thanks to Cédric Beust, author of JCommander which is used in Coffee-Graph.<br/>
Thanks to R.J. Lorimer for comments, suggestions, and code review.


## Technology Licensing
CoffeeScript, MIT: https://github.com/jashkenas/coffee-script/blob/master/LICENSE<br/>
JCommander, Apache 2.0 Software License: https://github.com/cbeust/jcommander/blob/master/license.txt<br/>
JUnit, Common Public License v1.0: http://www.junit.org/license<br/>
Rhino, Mozilla Public License v2.0: http://www.mozilla.org/MPL


## Coffee-Graph License - MIT
Coffee-Graph<br/>
Copyright(C) 2012, Matt Bolt, mbolt35@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.