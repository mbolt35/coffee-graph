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

import bolt.web.coffee.command.CoffeeGraphCommandParser;
import bolt.web.coffee.dependency.graph.CyclicDependencyException;
import bolt.web.coffee.exceptions.ChainedExportException;
import bolt.web.coffee.exceptions.NoValidCoffeeFilesException;
import bolt.web.coffee.exceptions.RequiredBuilderComponentException;
import bolt.web.coffee.io.*;
import bolt.web.coffee.util.CoffeeScript;

/**
 * This is the main class for the coffee-graph project.
 * 
 * @author Matt Bolt
 */
public class CoffeeGraph implements Runnable {

    public static void main(String[] arguments) {
        // Use default implementations if this class is used as main
        CoffeeGraphOptionsParser parser = new CoffeeGraphCommandParser(arguments);
        ExporterFactory exporters = new DefaultExporterFactory();

        CoffeeGraph coffeeGraph = new CoffeeGraph(parser, exporters);
        coffeeGraph.run();
    }

    public static String getVersionInfo() {
        return "[CoffeeGraph v" + CoffeeGraphVersion.Version + "]\n" +
               " +- Running: CoffeeScript v" + new CoffeeScript().getVersion() + "\n" +
               " +- Using: Java v" + System.getProperty("java.version") +
                    " from " + System.getProperty("java.vendor");
    }

    private final CoffeeGraphOptionsParser optionsParser;
    private final ExporterFactory exporters;

    public CoffeeGraph(CoffeeGraphOptionsParser optionsParser, ExporterFactory exporters) {
        this.optionsParser = optionsParser;
        this.exporters = exporters;
    }

    @Override
    public void run() {
        CoffeeGraphOptions options = optionsParser.parse();

        if (null == options || options.isHelp()) {
            optionsParser.usage();
            return;
        }

        if (options.isVersion()) {
            System.out.println(getVersionInfo());
            return;
        }

        try {
            new CoffeeScriptDependencyBuilder()
                .withTokensFrom(new CoffeeScriptLexer())
                .parsedWith(new CoffeeScriptMinParser())
                .exportedBy(exporters.exporterFor(options))
                .build(options.getSourceFiles());
        }
        catch (RequiredBuilderComponentException e) {
            // This is more of a developer error -- maybe be nice to have in ant/maven plugin dev or extensions
            e.printStackTrace();
        }
        catch (NoValidCoffeeFilesException e) {
            System.out.println("[CoffeeGraph Error: " + e.getMessage() + "]");
        }
        catch (ChainedExportException e) {
            for (Throwable throwable : e.getExceptions()) {
                System.out.println("[CoffeeGraph Error: " + throwable.getMessage() + "]");
            }
        }
        catch (CyclicDependencyException e) {
            System.out.println("[CoffeeGraph Error: " + e.getMessage() + "]");
        }
    }

}
