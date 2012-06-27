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

import bolt.web.coffee.command.CoffeeGraphCommand;
import bolt.web.coffee.command.FileConverterFactory;
import bolt.web.coffee.dependency.graph.CyclicDependencyException;
import bolt.web.coffee.exceptions.ChainedExportException;
import bolt.web.coffee.exceptions.NoValidCoffeeFilesException;
import bolt.web.coffee.exceptions.RequiredBuilderComponentException;
import bolt.web.coffee.io.CoffeeScriptLexer;
import bolt.web.coffee.io.CoffeeScriptMinParser;
import bolt.web.coffee.io.Exporter;
import bolt.web.coffee.io.exporters.ChainedExporter;
import bolt.web.coffee.io.exporters.CoffeeScriptCompileExporter;
import bolt.web.coffee.io.exporters.DependencyTreeExporter;
import bolt.web.coffee.io.exporters.ListFilesExporter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * This is the main class for the coffee-graph project.
 * 
 * @author Matt Bolt
 */
public class CoffeeGraph implements Runnable {

    public static void main(String[] arguments) {
        CoffeeGraphCommand command = new CoffeeGraphCommand();

        JCommander commander = new JCommander(command);
        commander.setProgramName("CoffeeGraph");
        commander.addConverterFactory(new FileConverterFactory());

        try {
            commander.parse(arguments);
        }
        catch (ParameterException e) {
            commander.usage();
        }

        if (command.help) {
            commander.usage();
            return;
        }

        Thread thread = new Thread(new CoffeeGraph(command));
        thread.start();
    }

    private final CoffeeGraphCommand command;

    public CoffeeGraph(CoffeeGraphCommand command) {
        this.command = command;
    }

    @Override
    public void run() {
        try {
            new CoffeeScriptDependencyBuilder()
                .withTokensFrom(new CoffeeScriptLexer())
                .parsedWith(new CoffeeScriptMinParser())
                .exportedBy(getExporter())
                .build(command.files);
        }
        catch (RequiredBuilderComponentException e) {
            e.printStackTrace();
        }
        catch (NoValidCoffeeFilesException e) {
            System.out.println(e.getMessage());
        }
        catch (ChainedExportException e) {
            for (Throwable throwable : e.getExceptions()) {
                System.out.println("Error: " + throwable.getMessage());
            }
        }
        catch (CyclicDependencyException e) {
            e.printStackTrace();
        }
    }

    private Exporter getExporter() {
        ChainedExporter exporter = new ChainedExporter();

        if (command.print) {
            exporter.addExporter(new ListFilesExporter(false));
        }

        if (command.println) {
            exporter.addExporter(new ListFilesExporter(true));
        }

        if (command.tree) {
            exporter.addExporter(new DependencyTreeExporter());
        }

        if (command.compile) {
            exporter.addExporter(new CoffeeScriptCompileExporter(command.outputFile));
        }

        return exporter;
    }
}
