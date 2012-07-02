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
import bolt.web.coffee.types.CoffeeScriptType;
import bolt.web.coffee.types.CoffeeSymbolType;
import bolt.web.coffee.types.CoffeeTokenType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is used to manage all of the dependencies parsed from the coffee-script source files. The
 * {@code generateGraph} method will drill-down into the {@code CoffeeTree}, extracting any identifiers that are visible
 * to the other coffee-script source files. It will also examine the rest of the current file to find any potential
 * identifier references stemming from our current source identifier. We refer to these as {@code OutgoingReferences},
 * or edges from our current identifier to another identifier.
 *
 * <p>
 * Due to the fact that the target identifier may not have been parsed yet, we create a record of this instance, such
 * that we can resolve it during our second pass.
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
                CoffeeIdentifier identifier = new CoffeeIdentifier(idToken.getValue(), file, scope.getDepth());

                if (isAssignedToGlobal(scope, idToken)) {
                    scope.addAssignment(idToken);
                }

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
    private Set<DependencyReference<CoffeeIdentifier>> findReferencesIn(CoffeeScope scope) {
        return findReferencesIn(scope, new HashSet<String>());
    }

    /**
     * Recursively search each inner-scope for references to identifiers outside of the current scope.
     *
     * @param scope The {@code CoffeeScope} instance to search.
     *
     * @param overrides If any of the global identifiers are overridden in a different scope, we need to ensure that any
     * identifiers within that scope do not create a dependency with the global identifier. We collect "overrides" as we
     * recurse and pass them onto the next inner scope reference search. It's important to note that <strong>This
     * Set instance is *not* modified.</strong>
     *
     * @return A list of the identifier references found in the existing and inner scopes.
     */
    private Set<DependencyReference<CoffeeIdentifier>> findReferencesIn(CoffeeScope scope, Set<String> overrides) {
        Set<DependencyReference<CoffeeIdentifier>> references = new HashSet<DependencyReference<CoffeeIdentifier>>();
        Set<String> localOverrides = new HashSet<String>(overrides);

        // Check local scope
        for (CoffeeToken token : scope.tokensFor(CoffeeTokenType.Identifier)) {
            // Check the current scope override identifiers to see if this token fails
            if (localOverrides.contains(token.getValue())) {
                continue;
            }

            // Check for a global identifier with the same name -- ensure that this identifier is to be ignored
            // throughout the scope of this token.
            if (dependencies.canResolve(token.getValue()) && isAssigned(scope, token)) {
                localOverrides.add(token.getValue());
                continue;
            }

            // If we determine that the identifier reference is legitimate, add a dependency reference
            if (isValidReference(scope, token)) {
                references.add(reference.to(token.getValue()));
            }
        }

        // Recursively search each inner scope
        for (CoffeeScope innerScope : scope.getScopes()) {
            List<CoffeeToken> tokens = innerScope.tokensFor(CoffeeTokenType.Identifier);
            Set<String> innerOverrides = new HashSet<String>();

            for (CoffeeToken token : tokens) {
                // Check the current scope override identifiers to see if this token fails
                if (localOverrides.contains(token.getValue()) || innerOverrides.contains(token.getValue())) {
                    continue;
                }

                // Check for a global identifier with the same name -- ensure that this identifier is to be ignored
                // throughout the scope of this token.
                if (dependencies.canResolve(token.getValue()) && isAssigned(innerScope, token)) {
                    innerOverrides.add(token.getValue());
                    continue;
                }

                // If we determine that the identifier reference is legitimate, add a dependency reference
                if (isValidReference(innerScope, token)) {
                    references.add(reference.to(token.getValue()));
                }
            }

            // Pass existing overrides with the inner overrides found in this scope, and recurse
            innerOverrides.addAll(localOverrides);
            references.addAll( findReferencesIn(innerScope, innerOverrides) );
        }

        return references;
    }

    // This will resolve anything that looks like it may reference a global dependency, which is ok because
    // it will not show up in the global list of identifiers. They will then be ignored.
    private boolean isValidReference(CoffeeScope scope, CoffeeToken token) {
        CoffeeToken after = scope.after(token);
        CoffeeToken before = scope.before(token);

        return isFieldReference(before, after) || isFunctionReference(scope, after) || isClassReference(before);
    }

    private boolean isFieldReference(CoffeeToken before, CoffeeToken after) {
        boolean isDotAfter = null != after && isDot(after);

        return isDotAfter && (null == before || !isDot(before));
    }

    private boolean isClassReference(CoffeeToken before) {
        return null != before && (isNew(before) || isExtends(before));
    }

    private boolean isFunctionReference(CoffeeScope scope, CoffeeToken after) {
        if (null == after || !isCallStart(after)) {
            return false;
        }

        CoffeeToken next = scope.after(after);
        while (null != next) {
            if (isCallEnd(next)) {
                return true;
            }
            next = scope.after(next);
        }

        return false;
    }

    private boolean isAssigned(CoffeeScope scope, CoffeeToken token) {
        return isAssignment(scope.after(token));
    }

    private boolean isGlobalScoped(CoffeeScope scope, CoffeeToken token) {
        CoffeeToken before = scope.before(token);
        if (null == before) {
            return false;
        }

        return isAt(before)
            || isThisDot(scope, before)
            || isWindowDot(scope, before)
            || isGlobalDot(scope, before);
    }

    private boolean isAssignedToGlobal(CoffeeScope scope, CoffeeToken token) {
        if (!isAssigned(scope, token)) {
            return false;
        }

        CoffeeToken after = scope.after(token);
        while(null != after && !isTerminator(after)) {
            if (isExports(after) || isAt(after) || isThis(after) || isWindow(after) || hasGlobalAssignment(scope, after)) {
                after = scope.after(after);
                if (!isDot(after)) {
                    return true;
                }
                continue;
            }
            after = scope.after(after);
        }

        return true;
    }

    private boolean hasGlobalAssignment(CoffeeScope scope, CoffeeToken token) {
        return null != token && scope.hasAssignment(token);
    }

    private boolean isTerminator(CoffeeToken token) {
        return null != token && CoffeeTokenType.Terminator == token.getType();
    }

    private boolean isThisDot(CoffeeScope scope, CoffeeToken token) {
        return null != token && isDot(token) && isThis(scope.before(token));
    }

    private boolean isWindowDot(CoffeeScope scope, CoffeeToken token) {
        return null != token && isDot(token) && isWindow(scope.before(token));
    }

    private boolean isGlobalDot(CoffeeScope scope, CoffeeToken token) {
        return null != token && isDot(token) && hasGlobalAssignment(scope, scope.before(token));
    }

    private boolean isNew(CoffeeToken token) {
        return null != token && CoffeeScriptType.typeFor(token.getValue()) == CoffeeSymbolType.New;
    }

    private boolean isExtends(CoffeeToken token) {
        return null != token && CoffeeTokenType.Extends == token.getType();
    }

    private boolean isAssignment(CoffeeToken token) {
        return null != token
            && (CoffeeSymbolType.Assignment == token.getType() || CoffeeTokenType.CompoundAssign == token.getType());
    }

    private boolean isThis(CoffeeToken token) {
        return null != token && CoffeeTokenType.This == token.getType();
    }

    public boolean isExports(CoffeeToken token) {
        return null != token && CoffeeScriptType.typeFor(token.getValue()) == CoffeeSymbolType.Exports;
    }

    private boolean isWindow(CoffeeToken token) {
        return null != token && CoffeeScriptType.typeFor(token.getValue()) == CoffeeSymbolType.Window;
    }

    private boolean isAt(CoffeeToken token) {
        return null != token && CoffeeSymbolType.At == token.getType();
    }

    private boolean isDot(CoffeeToken token) {
        return null != token && CoffeeSymbolType.Dot == token.getType();
    }

    private boolean isCallStart(CoffeeToken token) {
        return null != token && CoffeeTokenType.CallStart == token.getType();
    }

    private boolean isCallEnd(CoffeeToken token) {
        return null != token && CoffeeTokenType.CallEnd == token.getType();
    }
}
