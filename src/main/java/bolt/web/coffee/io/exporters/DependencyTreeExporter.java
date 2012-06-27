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

package bolt.web.coffee.io.exporters;

import bolt.web.coffee.dependency.CoffeeIdentifier;
import bolt.web.coffee.dependency.graph.CyclicDependencyException;
import bolt.web.coffee.dependency.graph.DependencyGraph;
import bolt.web.coffee.dependency.graph.GraphUtils;
import bolt.web.coffee.io.Exporter;

/**
 * This class simply exports the ordered dependency "tree" to standard out.
 * 
 * @author Matt Bolt
 */
public class DependencyTreeExporter implements Exporter {

    @Override
    public void export(DependencyGraph<CoffeeIdentifier> graph) {
        try {
            System.out.println("\nCoffee-Graph Object Dependency Tree");
            System.out.println("===================================");
            GraphUtils.printDependencies(graph);
            System.out.println("===================================");
        }
        catch (CyclicDependencyException e) {
            throw new RuntimeException(e);
        }
    }
}
