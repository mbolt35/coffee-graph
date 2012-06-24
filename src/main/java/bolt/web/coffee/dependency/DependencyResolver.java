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

package bolt.web.coffee.dependency;

import java.util.Set;

/**
 * An implementation prototype for an object which accepts {@link NamedDependency} implementers and provides look-up
 * methods for resolving the added references.
 *
 * @author Matt Bolt
 */
public interface DependencyResolver<T extends NamedDependency> {

    /**
     * This method adds a dependency reference for resolution.
     *
     * @param dependency The {@link NamedDependency} implementer to add.
     */
    void add(T dependency);

    /**
     * This method determines whether or not the identifier name passed can be resolved or not.
     *
     * @param name The name of the identifier to check.
     *
     * @return {@code true} if the identifier name can be resolved.
     */
    boolean canResolve(String name);

    /**
     * This method resolves a dependency by name and scope depth.
     *
     * @param name The name of the identifier.
     *
     * @return The {@link NamedDependency} implementer matching both the name and depth.
     */
    T resolve(String name);

    /**
     * This method returns a {@code Set} of all the dependencies managed by the implementation
     */
    Set<T> getAll();
}
