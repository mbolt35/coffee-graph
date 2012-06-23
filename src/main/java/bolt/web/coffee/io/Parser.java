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

import bolt.web.coffee.tree.CoffeeTree;

import java.io.File;
import java.util.List;

/**
 * Implementation prototype for a coffee-script parser.
 *
 * @author Matt Bolt
 */
public interface Parser {

    /**
     * This method accepts a list of {@code File} instances and parses each into a single {@code CoffeeTree} instance,
     * where each {@code File} has it's own {@code CoffeeScope} for evaluation.
     *
     * @param files The coffee-script {@code File} instances to parse.
     *
     * @return A {@code CoffeeTree} instance containing each of the parsed files, segmented by file scope.
     */
    CoffeeTree parse(List<File> files);

}
