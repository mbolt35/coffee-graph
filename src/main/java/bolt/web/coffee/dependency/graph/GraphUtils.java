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


package bolt.web.coffee.dependency.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Utility methods that can be used with a graph data structure.
 *
 * @author Matt Bolt
 */
public final class GraphUtils {

    /**
     * This method performs a topological sort on the {@code DependencyGraph} and returns the sorted {@code List}. This
     * method does not alter the original graph.
     *
     * @param dependencyGraph The {@code DependencyGraph} to sort topologically.
     *
     * @return A {@code List} containing the sorted nodes.
     *
     * @throws CyclicDependencyException This exception is thrown when cyclic dependencies are found within the graph.
     */
    public static <T> List<T> topologicalSort(DependencyGraph<T> dependencyGraph) throws CyclicDependencyException {
        DependencyGraph<T> graph = dependencyGraph.copy();
        List<T> identifiers = new ArrayList<T>();
        Set<T> noIncoming = graph.getRoots();

        while (!noIncoming.isEmpty()) {
            T n = shift(noIncoming);
            identifiers.add(0, n);

            for (T m : graph.outgoingFor(n)) {
                graph.remove(n, m);

                if (graph.incomingFor(m).isEmpty()) {
                    noIncoming.add(m);
                }
            }
        }

        if (!graph.getEdges().isEmpty()) {
            List<Edge<T>> edges = new ArrayList<Edge<T>>(graph.getEdges());

            throw new CyclicDependencyException(edges);
        }

        return identifiers;
    }

    /**
     * This helper method gets the first elements of the set, removes it, and returns it.
     *
     * @param set The set to shift the first element found off the set.
     *
     * @return The {@code T} element shifted off.
     */
    private static <T> T shift(Set<T> set) {
        Iterator<T> iterator = set.iterator();
        if (iterator.hasNext()) {
            T next = iterator.next();
            set.remove(next);
            return next;
        }

        return null;
    }
}
