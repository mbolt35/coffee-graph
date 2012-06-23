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

import java.util.*;

/**
 * @author Matt Bolt
 */
public class DependencyGraph<T> {

    private final Set<T> nodes;

    private final Map<T, Set<T>> outgoing;
    private final Map<T, Set<T>> incoming;

    private final Set<Edge<T>> edges;

    public DependencyGraph() {
        this(new HashSet<T>(), new HashMap<T, Set<T>>(), new HashMap<T, Set<T>>(), new HashSet<Edge<T>>());
    }

    private DependencyGraph(Set<T> nodes, Map<T, Set<T>> outgoing, Map<T, Set<T>> incoming, Set<Edge<T>> edges) {
        this.nodes = nodes;
        this.outgoing = outgoing;
        this.incoming = incoming;
        this.edges = edges;
    }

    public void add(T node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
        }
    }

    public void add(T from, T to) {
        add(new Edge<T>(from, to));
    }

    public void add(Edge<T> edge) {
        T from = edge.getFrom();
        T to = edge.getTo();

        if (!outgoing.containsKey(from)) {
            outgoing.put(from, new HashSet<T>());
        }
        if (!incoming.containsKey(to)) {
            incoming.put(to, new HashSet<T>());
        }

        add(from);
        add(to);

        outgoing.get(from).add(to);
        incoming.get(to).add(from);

        edges.add(edge);
    }

    public void remove(T from, T to) {
        remove(new Edge<T>(from, to));
    }

    public void remove(Edge<T> edge) {
        T from = edge.getFrom();
        T to = edge.getTo();

        if (outgoing.containsKey(from)) {
            outgoing.get(from).remove(to);
        }

        if (incoming.containsKey(to)) {
            incoming.get(to).remove(from);
        }

        edges.remove(edge);
    }

    public Set<T> outgoingFor(T item) {
        Set<T> out = new HashSet<T>();

        if (outgoing.containsKey(item)) {
            out.addAll(outgoing.get(item));
        }

        return out;
    }

    public Set<T> incomingFor(T item) {
        Set<T> in = new HashSet<T>();

        if (incoming.containsKey(item)) {
            in.addAll(incoming.get(item));
        }

        return in;
    }

    public DependencyGraph<T> copy() {
        return new DependencyGraph<T>(
            new HashSet<T>(nodes),
            new HashMap<T, Set<T>>(outgoing),
            new HashMap<T, Set<T>>(incoming),
            new HashSet<Edge<T>>(edges));
    }

    public Set<T> getRoots() {
        Set<T> result = new HashSet<T>(nodes);
        result.removeAll(incoming.keySet());
        return result;
    }

    public Set<Edge<T>> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

}
