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
import bolt.web.coffee.types.CoffeeType;

import java.util.*;

/**
 * This class represents lexical scope defined by a root, a file, or an indention. It maintains records of the tokens
 * contained within the scope as well as other scopes.
 * 
 * @author Matt Bolt
 */
public class CoffeeScope {

    private static final CoffeeToken ROOT = new CoffeeToken(CoffeeTokenType.Identifier, "ROOT", 0);

    private final CoffeeToken identifier;
    private final int depth;

    private final List<CoffeeToken> tokens = new ArrayList<CoffeeToken>();
    private final Map<CoffeeToken, CoffeeScope> scopeIdentifiers = new HashMap<CoffeeToken, CoffeeScope>();
    private final Map<CoffeeType, List<CoffeeToken>> byType = new HashMap<CoffeeType, List<CoffeeToken>>();

    private final Set<String> assignments = new HashSet<String>();

    CoffeeScope() {
        this(ROOT, 0);
    }

    public CoffeeScope(CoffeeToken identifier, int depth) {
        this.identifier = identifier;
        this.depth = depth;
    }

    public void add(CoffeeToken token) {
        if (!byType.containsKey(token.getType())) {
            byType.put(token.getType(), new ArrayList<CoffeeToken>());
        }

        byType.get(token.getType()).add(token);
        tokens.add(token);
    }

    public void addAssignment(CoffeeToken token) {
        if (assignments.contains(token.getValue())) {
            return;
        }

        assignments.add(token.getValue());
    }

    public boolean hasAssignment(CoffeeToken token) {
        return assignments.contains(token.getValue());
    }

    public void add(CoffeeScope scope) {
        scopeIdentifiers.put(scope.getIdentifier(), scope);
    }

    public CoffeeToken getIdentifier() {
        return identifier;
    }

    public List<CoffeeScope> getScopes() {
        return new ArrayList<CoffeeScope>(scopeIdentifiers.values());
    }

    public List<CoffeeToken> tokensFor(CoffeeType type) {
        if (byType.containsKey(type)) {
            return Collections.unmodifiableList(byType.get(type));
        }

        return new ArrayList<CoffeeToken>();
    }

    public CoffeeToken before(CoffeeToken token) {
        return tokenOffsetBy(token, -1);
    }

    public CoffeeToken after(CoffeeToken token) {
        return tokenOffsetBy(token, 1);
    }

    public int getDepth() {
        return depth;
    }

    private CoffeeToken tokenOffsetBy(CoffeeToken token, int amount) {
        int index = tokens.indexOf(token);
        int offset = index + amount;

        // null return for invalid range or not existent token for scope
        if (offset < 0 || offset >= tokens.size()) {
            return null;
        }

        return tokens.get(offset);
    }

}
