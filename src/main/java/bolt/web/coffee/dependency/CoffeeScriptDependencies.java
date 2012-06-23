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

import bolt.web.coffee.dependency.graph.DependencyGraph;
import bolt.web.coffee.io.CoffeeToken;
import bolt.web.coffee.tree.CoffeeScope;
import bolt.web.coffee.tree.CoffeeTree;
import bolt.web.coffee.types.CoffeeSymbolType;
import bolt.web.coffee.types.CoffeeTokenType;

import java.io.File;
import java.util.*;

/**
 * This class is used to manage all of the dependencies parsed from the coffee-script source files. The
 * {@code generateGraph} method will drill-down into the {@code CoffeeTree}, extracting any identifiers that are visible
 * to the other coffee-script source files. It will also examine the rest of the current file to find any potential
 * identifier references stemming from our current source identifier. We refer to these as {@code OutgoingReferences},
 * or edges from our current identifier to another identifier.
 *
 * <p>Due to the fact that the target identifier may not have been parsed yet, we create a record of this instance, such
 * that we can resolve it during our second pass. </p>
 *
 * @author Matt Bolt
 */
public class CoffeeScriptDependencies {

    private final DependencyManager<CoffeeIdentifier> dependencies;
    private final DependencyReferenceFactory<CoffeeIdentifier> reference;

    public CoffeeScriptDependencies() {
        this.dependencies = new DependencyManager<CoffeeIdentifier>();
        this.reference = new DependencyReferenceFactory<CoffeeIdentifier>(this.dependencies);
    }

    public DependencyGraph<CoffeeIdentifier> generateGraph(CoffeeTree tree) {

        DependencyGraph<CoffeeIdentifier> graph = new DependencyGraph<CoffeeIdentifier>();

        List<OutgoingReferences<CoffeeIdentifier>> outgoing = new ArrayList<OutgoingReferences<CoffeeIdentifier>>();
        Set<File> files = tree.getFiles();

        // 1. Iterate through each file and perform the first pass edge creation for the dependency graph
        for (File file : files) {
            CoffeeScope scope = tree.scopeFor(file);
            List<CoffeeToken> idTokens = scope.tokensFor(CoffeeTokenType.Identifier);

            if (null == idTokens) {
                continue;
            }

            // Collect all of the global identifiers, determine any potential identifiers referenced
            for (CoffeeToken idToken : idTokens) {
                CoffeeIdentifier identifier = new CoffeeIdentifier(idToken.getValue(), file);

                if (isGlobalScoped(scope, idToken)) {
                    dependencies.add(identifier);
                }

                graph.add(identifier);
                outgoing.add(new OutgoingReferences<CoffeeIdentifier>(identifier, findReferencesIn(scope)));
            }
        }

        // 2. Resolve the other side of the dependencies -- this is the brute force alternative to look ahead parsing
        //    Probably lots of ways to optimize this!

        // After resolving all identifiers, resolve the outgoing references and add them to the dependency graph
        // Also, ignore any "identifiers" that fail to resolve. These are due to my lazy parsing below.
        for (OutgoingReferences<CoffeeIdentifier> references : outgoing) {
            CoffeeIdentifier parentIdentifier = references.getItem();

            for (DependencyReference<CoffeeIdentifier> idReference : references.getOutgoing()) {
                CoffeeIdentifier resolvedReference = idReference.get();
                if (null == resolvedReference) {
                    continue;
                }

                if (resolvedReference.getFile().equals(parentIdentifier.getFile())) {
                    continue;
                }

                graph.add(parentIdentifier, resolvedReference);
            }
        }

        return graph;
    }

    /**
     * Recursively search each inner-scope for references to identifiers outside of the current scope.
     *
     * @param scope The {@code CoffeeScope} instance to search.
     *
     * @return A list of the identifier references found in the existing and inner scopes.
     */
    private List<DependencyReference<CoffeeIdentifier>> findReferencesIn(CoffeeScope scope) {
        // TODO: We need to use a better key for resolving references -- the current key is just the identifier's value,
        // TODO: which is the name. The key *should* contain a reference to the scope it exists in such that identifiers
        // TODO: defined in the outer scope don't resolve to a potential global scoped identifier.

        /* Example */
        /* The following would create an edge between Foo.coffee and Bar.coffee even though they're autonomous */
        /*
            <File: Foo.coffee>
            class Foo
              a: "a"
              constructor: ->

            <File: Bar.coffee>
            aMethod = ->
              anotherInnerMethod = ->
                Foo = "someStringValue"

                alert Foo

        */

        List<DependencyReference<CoffeeIdentifier>> references = new ArrayList<DependencyReference<CoffeeIdentifier>>();

        // Search the current scope for any identifier references, then validate the *type* of reference
        List<CoffeeToken> localTokens = scope.tokensFor(CoffeeTokenType.Identifier);
        for (CoffeeToken token : localTokens) {
            if (isValidReference(scope, token)) {
                references.add(reference.to(token.getValue()));
            }
        }

        // Recursively search each inner scope
        for (CoffeeScope innerScope : scope.getScopes()) {
            List<CoffeeToken> tokens = innerScope.tokensFor(CoffeeTokenType.Identifier);

            for (CoffeeToken token : tokens) {
                if (isValidReference(innerScope, token)) {
                    references.add(reference.to(token.getValue()));
                }
            }

            references.addAll( findReferencesIn(innerScope) );
        }

        return references;
    }


    // This will resolve anything that looks like it may reference a global dependency, which is ok because
    // it will not show up in the global list of identifiers. They will then be ignored.
    private boolean isValidReference(CoffeeScope scope, CoffeeToken token) {
        CoffeeToken after = scope.after(token);
        CoffeeToken before = scope.before(token);

        if (null == before && null != after) {
            return isDot(after);
        }

        return before != null && (isNew(before) || isExtends(before));
    }

    private boolean isGlobalScoped(CoffeeScope scope, CoffeeToken token) {
        CoffeeToken before = scope.before(token);

        return null != before && (isAt(before) || isThisDot(scope, before));
    }

    private boolean isThisDot(CoffeeScope scope, CoffeeToken token) {
        return null != token && isDot(token) && isThis(scope.before(token));
    }

    private boolean isNew(CoffeeToken token) {
        return null != token && token.getValue().equals("new");
    }

    private boolean isExtends(CoffeeToken token) {
        return null != token && CoffeeTokenType.Extends == token.getType();
    }

    private boolean isThis(CoffeeToken token) {
        return null != token && CoffeeTokenType.This == token.getType();
    }

    private boolean isAt(CoffeeToken token) {
        return null != token && CoffeeSymbolType.At == token.getType();
    }

    private boolean isDot(CoffeeToken token) {
        return null != token && CoffeeSymbolType.Dot == token.getType();
    }
}
