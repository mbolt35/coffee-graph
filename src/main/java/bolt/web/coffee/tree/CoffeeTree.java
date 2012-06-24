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

package bolt.web.coffee.tree;

import bolt.web.coffee.io.CoffeeToken;
import bolt.web.coffee.types.CoffeeTokenType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


/**
 * This class represents a tree object that organizes tokens based on file and indentation scope only. This is *not* an
 * accurate abstract syntax tree because we're ignoring all other legitimate coffee-script scope-generating syntax.
 * Since the focus of this project is not to analyze coffee script semantics, we'll not be solving that problem. Instead,
 * we'll parse the identifiers and various scope generating symbols that will allow us to analyze dependencies between
 * coffee script files.
 *
 * @author Matt Bolt
 */
public class CoffeeTree {

    private final CoffeeScope global = new CoffeeScope();
    private final Map<File, CoffeeScope> byFile = new HashMap<File, CoffeeScope>();
    private final Stack<CoffeeScope> scopes = new Stack<CoffeeScope>();

    private CoffeeScope currentFileScope;


    public CoffeeTree() {
        // Initialize our scope stack with global scope
        scopes.push(global);
    }

    /**
     * This method adds a coffee {@code File} instance to the tree. This creates a coffee identifier token representing
     * the file scope, such that we'll be able to tie the file to a specific scope later. Adding a file will automatically
     * pop the previous file scope if one exists.
     *
     * <p>Additionally, each file that is added will map to it's respective scope, which can be retrieved by using the
     * {@link #scopeFor(java.io.File)} method.</p>
     *
     * @param file The .coffee {@code File} instance to generate scope for.
     */
    public void add(File file) {
        if (null != currentFileScope) {
            if (currentFileScope != scopes.pop()) {
                throw new RuntimeException("Tree scopes became unbalanced - file scope added to non-global scope");
            }
        }

        if (getCurrentScope() != global) {
            throw new RuntimeException("File scopes can only be added to the global scope instance!");
        }

        currentFileScope = new CoffeeScope(new CoffeeToken(CoffeeTokenType.Identifier, file.getName()), scopes.size());
        byFile.put(file, currentFileScope);
        scopes.push(currentFileScope);
    }

    /**
     * This method adds a token to the current scope of the tree. If the token type is an indent or outdent, it automatically
     * pushes and pops scope.
     *
     * @param token The {@code CoffeeToken} to add to the current scope.
     */
    public void add(CoffeeToken token) {
        if (token.getType() == CoffeeTokenType.Indent) {
            scopes.push(new CoffeeScope(token, scopes.size()));
            return;
        }

        if (token.getType() == CoffeeTokenType.Outdent) {
            CoffeeScope scope = scopes.pop();
            getCurrentScope().add(scope);
            return;
        }

        getCurrentScope().add(token);
    }

    /**
     * This method returns the {@link CoffeeScope} associated with the {@code File} instance.
     *
     * @param file The {@code File} instance to find the scope for.
     *
     * @return {@link CoffeeScope} for the {@code File} instance.
     */
    public CoffeeScope scopeFor(File file) {
        return byFile.get(file);
    }

    /**
     * This method returns all of the {@code File} instances added to the tree.
     *
     * @return A {@code Set} of {@code File} instances added to the tree.
     */
    public Set<File> getFiles() {
        return byFile.keySet();
    }

    private CoffeeScope getCurrentScope() {
        return scopes.peek();
    }

}
