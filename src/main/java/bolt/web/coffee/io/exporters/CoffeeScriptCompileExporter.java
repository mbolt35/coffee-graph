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

package bolt.web.coffee.io.exporters;

import bolt.web.coffee.dependency.CoffeeIdentifier;
import bolt.web.coffee.dependency.graph.CyclicDependencyException;
import bolt.web.coffee.dependency.graph.DependencyGraph;
import bolt.web.coffee.dependency.graph.GraphUtils;
import bolt.web.coffee.util.CoffeeScript;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This {@link bolt.web.coffee.io.Exporter} implementation orders the dependencies, then executes a similar function to
 * {@code $ coffee --join <output> --compile <files>}
 *
 * @author Matt Bolt
 */
public class CoffeeScriptCompileExporter extends AbstractExporter {

    private final File outputFile;

    public CoffeeScriptCompileExporter(File outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public void export(DependencyGraph<CoffeeIdentifier> graph) {
        CoffeeScript coffeeScript = new CoffeeScript();

        // Process all of the identifiers and filter any duplicate files from identifier dependencies.
        List<CoffeeIdentifier> identifiers = null;
        try {
            identifiers = trimDuplicateFiles( GraphUtils.topologicalSort(graph) );
        }
        catch (CyclicDependencyException e) {
            throw new RuntimeException(e);
        }

        // Ensure that we have a fresh output file -- delete if it exists, and create/recreate
        try {
            if (outputFile.exists()) {
                if (!outputFile.delete()) {
                    throw new RuntimeException("Output file: " + outputFile.getName() + " could not be used at this time.");
                }
            }

            if (null != outputFile.getParentFile() && !outputFile.getParentFile().mkdirs()) {
                throw new RuntimeException("Could not create the file for export.");
            }

            if (!outputFile.createNewFile() || !outputFile.isFile()) {
                throw new RuntimeException("Could not create output file for export.");
            }

            System.out.println("Compiling CoffeeScript -> " + outputFile.getCanonicalPath());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Finally, compile each file in order and push the output to our file.
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(outputFile));

            // Write CoffeeScript version used
            out.write("// Compiled with CoffeeScript v" + coffeeScript.getVersion() + "\n\n");
            for (CoffeeIdentifier identifier : identifiers) {
                File coffeeSource = identifier.getFile();
                System.out.println("Compiling: " + coffeeSource.getName());

                String compiledJs = coffeeScript.compile(coffeeSource);
                out.write(compiledJs + "\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Coffee-Script compiled successfully.");
    }
}
