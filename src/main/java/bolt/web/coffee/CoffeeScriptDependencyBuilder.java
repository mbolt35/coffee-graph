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

package bolt.web.coffee;


import bolt.web.coffee.dependency.CoffeeIdentifier;
import bolt.web.coffee.dependency.CoffeeScriptDependencies;
import bolt.web.coffee.dependency.graph.CyclicDependencyException;
import bolt.web.coffee.dependency.graph.DependencyGraph;
import bolt.web.coffee.dependency.graph.GraphUtils;
import bolt.web.coffee.exceptions.RequiredBuilderComponentException;
import bolt.web.coffee.io.Exporter;
import bolt.web.coffee.io.Lexer;
import bolt.web.coffee.io.Parser;
import bolt.web.coffee.tree.CoffeeTree;
import bolt.web.coffee.util.FileHelper;
import bolt.web.coffee.util.FileType;

import java.io.File;
import java.util.List;

/**
 * @author Matt Bolt
 */
public class CoffeeScriptDependencyBuilder {

    private Lexer coffeeLexer;
    private Parser coffeeParser;
    private Exporter coffeeExporter;

    public CoffeeScriptDependencyBuilder() {

    }

    public CoffeeScriptDependencyBuilder withTokensFrom(Lexer coffeeLexer) {
        this.coffeeLexer = coffeeLexer;
        return this;
    }

    public CoffeeScriptDependencyBuilder parsedWith(Parser coffeeParser) {
        this.coffeeParser = coffeeParser;
        return this;
    }

    public CoffeeScriptDependencyBuilder exportedBy(Exporter coffeeExporter) {
        this.coffeeExporter = coffeeExporter;
        return this;
    }

    public void build(List<File> fileTargets) throws CyclicDependencyException, RequiredBuilderComponentException {
        check(coffeeLexer, "coffeeLexer", "withTokensFrom");
        check(coffeeParser, "coffeeParser", "parsedWith");
        check(coffeeExporter, "coffeeExporter", "exportedBy");

        // Create a new dependency management instance
        CoffeeScriptDependencies dependencies = new CoffeeScriptDependencies();

        // 1. Collect all of the files in each directory provided
        List<File> files = FileHelper.collectFiles(fileTargets, FileType.Coffee);

        // 2. Build CoffeeScript Tree
        CoffeeTree tree = coffeeParser.parse(coffeeLexer, files);

        // 3. Generate Dependency Graph, Execute a Topological Sort
        DependencyGraph<CoffeeIdentifier> graph = dependencies.generateGraph(tree);
        List<CoffeeIdentifier> identifiers = GraphUtils.topologicalSort(graph);

        // 4. Export
        coffeeExporter.export(identifiers);

        //Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //clipboard.setContents(new StringSelection(command), null);
    }

    private <T> void check(T instance, String name, String methodName) throws RequiredBuilderComponentException {
        if (null == instance) {
            throw new RequiredBuilderComponentException(name, methodName);
        }
    }

}
