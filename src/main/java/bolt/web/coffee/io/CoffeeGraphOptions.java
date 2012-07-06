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

package bolt.web.coffee.io;

import java.io.File;
import java.util.List;

/**
 * Implementation prototype for the output options generated by Coffee-Graph.
 *
 * @author Matt Bolt
 */
public interface CoffeeGraphOptions {

    /**
     * Prints the file order on a single line, single-space delimited.
     *
     * @return {@code true} if Coffee-Graph should output the file order on a single line.
     */
    boolean isPrint();

    /**
     * Prints the file order on multiple lines, one per line.
     *
     * @return {@code true} if Coffee-Graph should output the file order, one file per line.
     */
    boolean isPrintLine();

    /**
     * Prints an expanded identifier dependency tree for the CoffeeScript source provided.
     *
     * @return {@code true} if Coffee-Graph should output the dependency tree.
     */
    boolean isPrintTree();

    /**
     * Prints the input options for Coffee-Graph
     *
     * @return {@code true} if Coffee-Graph should output the usage.
     */
    boolean isHelp();

    /**
     * Prints the Coffee-Graph, CoffeeScript, and Java version information.
     *
     * @return {@code true} if Coffee-Graph should output the version information.
     */
    boolean isVersion();

    /**
     * Compiles the input CoffeeScript source and outputs a single javascript file.
     *
     * @return {@code true} if CoffeeGraph should order and compile the CoffeeScript source.
     */
    boolean isCompile();

    /**
     * Omits the javascript security wrapper for compiled output.
     *
     * @return {@code true} if the CoffeeScript compiler should omit the security wrapper.
     */
    boolean isBare();

    /**
     * A {@code List} of {@code File} instances representing the source files or directories to build the dependency
     * graph for.
     */
    List<File> getSourceFiles();

    /**
     * The javascript output file for the Coffee-Graph compilation output.
     */
    File getOutputFile();
}
