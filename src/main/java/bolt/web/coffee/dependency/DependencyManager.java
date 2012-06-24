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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is just a simple wrapper for a {@code String} -> {@code NamedDependency} map that implements the
 * {@code DependencyResolver} interface for any dependencies referenced prior to creation.
 *
 * <p>
 * For example, since we're building a dependency graph for an unordered list of N files, we could easily run
 * across references to identifiers which haven't been defined yet. This is, incidentally the same problem we're
 * attempting to solve. Instead of parsing ahead looking for the identifier definition, we simply create a reference
 * which we'll resolve once the first pass is complete.
 *
 * <p>
 * Note that because we're using {@code String} identifiers for the names, this may cause issues if there are name
 * collisions between inner scopes. I'll revisit this later and apply a more defined key for reference look-up if there
 * is a problem.
 *
 * @author Matt Bolt
 */
public class DependencyManager<T extends NamedDependency> implements DependencyResolver<T> {

    private final Map<String, T> byName;

    public DependencyManager() {
        this.byName = new HashMap<String, T>();
    }

    @Override
    public void add(T dependency) {
        if (!byName.containsKey(dependency.getName())) {
            byName.put(dependency.getName(), dependency);
        }
    }

    @Override
    public boolean canResolve(String name) {
        return byName.containsKey(name);
    }

    @Override
    public T resolve(String name) {
        return byName.get(name);
    }

    @Override
    public Set<T> getAll() {
        return new HashSet<T>(byName.values());
    }
}
