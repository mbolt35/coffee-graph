////////////////////////////////////////////////////////////////////////////////
//
//  Coffee-Graph
//  Copyright(C) 2012 Matt Bolt
// 
//  Permission is hereby granted, free of charge, to any person obtaining a 
//  copy of this software and associated documentation files (the "Software"), 
//  to deal in the Software without restriction, including without limitation 
//  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
//  and/or sell copies of the Software, and to permit persons to whom the  
//  Software is furnished to do so, subject to the following conditions:
// 
//  The above copyright notice and this permission notice shall be included in 
//  all copies or substantial portions of the Software.
// 
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
//  THE SOFTWARE.
// 
////////////////////////////////////////////////////////////////////////////////

package bolt.web.coffee.command;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used with JCommander annotations to populate the runtime arguments passed to coffee graph.
 *
 * @author Matt Bolt
 */
public final class CoffeeGraphCommand {

    @Parameter(description = "The .coffee files (or directory) to build the dependency graph for.")
    public List<File> files = new ArrayList<File>();

    @Parameter(names = {"--print", "-p"}, description = "Prints the ordered .coffee files on a single line.")
    public boolean print = false;

    @Parameter(names = {"--println", "-pl"}, description = "Prints the ordered .coffee files, one per line.")
    public boolean println = false;

    @Parameter(names = {"--tree", "-t"}, description = "Prints a dependency tree.")
    public boolean tree = false;

    @Parameter(names = {"--compile", "-c"}, description = "Performs an ordered file join and compilation to CoffeeScript")
    public boolean compile = false;

    @Parameter(names = {"--output, -o" }, description = "The file to output the dependency graph to.")
    public File outputFile = new File("lib/coffee-graph.js");

    @Parameter(names = {"--help", "-h"}, description = "Show the coffee-graph command line usage.")
    public boolean help = false;

    public CoffeeGraphCommand() {

    }

    @Override
    public String toString() {
        return new StringBuilder("[CoffeeGraphCommand - ")
            .append("print: ").append(print).append(", ")
            .append("println: ").append(println).append(", ")
            .append("tree: ").append(tree).append(", ")
            .append("compile: ").append(compile).append(", ")
            .append("files: ").append(files).append(", ")
            .append("outputFile: ").append(outputFile).append(", ")
            .append("]").toString();
    }

}
