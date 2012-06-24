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

/**
 * Resolves a reference by name using a dependency resolver instance.
 *
 * @author Matt Bolt
 */
public class DependencyReference<T extends NamedDependency> {

    private final String name;
    private final DependencyResolver<T> resolver;

    public DependencyReference(String name, DependencyResolver<T> resolver) {
        this.name = name;
        this.resolver = resolver;
    }

    /**
     * This method returns an instance of the {@code T} instance if it's resolvable.
     *
     * @return The {@code T} instance referenced by this object if the instance is resolvable. Otherwise, {@code null}
     * is returned.
     */
    public T get() {
        return resolver.resolve(name);
    }

    @Override
    public String toString() {
        return "[DependencyReference - name: " + name + "]";
    }
}
