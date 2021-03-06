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

import bolt.web.coffee.io.CoffeeGraphOptions;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used with JCommander annotations to populate the runtime arguments passed to coffee graph.
 *
 * @author Matt Bolt
 */
public final class CoffeeGraphCommand implements CoffeeGraphOptions {

    @Parameter(description = "file.coffee ... [directory]")
    private List<File> files = new ArrayList<File>();

    @Parameter(names = {"--print", "-p"}, description = "Prints the ordered .coffee files on a single line.")
    private boolean print = false;

    @Parameter(names = {"--println", "-pl"}, description = "Prints the ordered .coffee files, one per line.")
    private boolean println = false;

    @Parameter(names = {"--tree", "-t"}, description = "Prints a dependency tree.")
    private boolean tree = false;

    @Parameter(names = {"--compile", "-c"}, description = "Performs an ordered file join and compilation to JavaScript")
    private boolean compile = false;

    @Parameter(names = {"--bare", "-b"}, description = "Compiles the CoffeeScript source without a security wrapper.")
    private boolean bare = false;

    @Parameter(names = {"-o" }, description = "The file to output the dependency graph to.")
    private File outputFile = new File("lib/coffee-graph.js");

    @Parameter(names = {"--help", "-h"}, description = "Show the coffee-graph command line usage.")
    private boolean help = false;

    @Parameter(names = {"--version", "-v"}, description = "Displays the current version of coffee-graph.")
    private boolean version = false;

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

    @Override
    public boolean isPrint() {
        return print;
    }

    @Override
    public boolean isPrintLine() {
        return println;
    }

    @Override
    public boolean isPrintTree() {
        return tree;
    }

    @Override
    public boolean isHelp() {
        return help;
    }

    @Override
    public boolean isVersion() {
        return version;
    }

    @Override
    public boolean isCompile() {
        return compile;
    }

    @Override
    public boolean isBare() {
        return bare;
    }

    @Override
    public List<File> getSourceFiles() {
        return files;
    }

    @Override
    public File getOutputFile() {
        return outputFile;
    }
}
