# Coffee-Graph

Coffee-Graph is an automated build tool for CoffeeScript, which is able to analyze large multi-file projects and output an **ordered** listing of the files using cross-file dependencies (if they exist). Coffee-Graph does not require the use of any external JS libraries like RequireJS; just plain vanilla CoffeeScript.


## Example
Let's say I have a directory `src` that contains three `.coffee` source files:

**src/A.coffee**

    class @A
      constructor: (@name) ->

      doSomething: -> 
        helloWorld()

**src/B.coffee**

    class @B
      constructor: (@name) ->
      
    @helloWorld = -> alert "Hello World!"

**src/C.coffee**

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

Using Coffee-Graph, we can generate the correct file ordering for successful execution in a browser:

    $ coffee-graph --print src
    src/B.coffee
    src/A.coffee
    src/C.coffee

Additionally, you can have Coffee-Graph forward compilation directly to CoffeeScript. See the demonstration javascript output at the bottom of this wiki.


## How It Works
Coffee-Graph was built in Java 6 and uses Mozilla's Rhino javascript engine to wrap the [CoffeeScript compiler](https://github.com/jashkenas/coffee-script/blob/master/extras/coffee-script.js). Each `.coffee` source file that is passed to the application is passed through the CoffeeScript.tokens() method. The tokens are then added to a pseudo Abstract Syntax Tree (AST), where each level of scope is generated based on `INDENT` and `OUTDENT` tokens. Parsing each file will also generate it's own file-level scope in the tree. 

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
Thanks to R.J. Lorimer for comments, suggestions, and code review.

## License
Coffee-Graph<br/>
Copyright(C) 2012, Matt Bolt, mbolt35@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.