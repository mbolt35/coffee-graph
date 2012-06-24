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

    /*
    private final GenerateCommand command;
    private final CoffeeEntities manager;
    private final CoffeeReferenceFactory reference;
    private final ExecutorService executor;

    private AtomicInteger completedCount = new AtomicInteger();

    private void writeIncludes(List<CoffeeClass> classes, String filePath, String targetPath, BufferedWriter writer)
        throws IOException
    {
        writer.write("# Inclusions based on dependency scan... #\n");

        Set<String> included = new HashSet<String>();
        for (CoffeeEntity entity : classes) {
            File file = entity.getDefinedIn();
            String fullPath = file.getAbsolutePath();
            if (fullPath.contains(filePath)) {
                fullPath = fullPath.substring(fullPath.indexOf(filePath) + filePath.length());
            }

            if (included.contains(fullPath)) {
                continue;
            }

            included.add(fullPath);
            String relativePath = targetPath + "/" + FileHelper.substituteFileType(fullPath, FileType.JavaScript);
            if (relativePath.contains("\\")) {
                relativePath = relativePath.replace("\\", "/");
            }

            writer.write("include \"" + relativePath + "\"\n");
        }

        writer.write("\n");
    }

    public CoffeeXGenerator() {
        this.command = new GenerateCommand();
        this.manager = new CoffeeEntities();
        this.reference = new CoffeeReferenceFactory(this.manager);
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void execute() {
        File[] allSrcFiles = command.files.toArray( new File[command.files.size()] );
        ArrayList<File> files = FileHelper.collectFiles(allSrcFiles, FileType.Coffee);

        System.out.println("Processing: " + files.size() + " files!");

        final CountDownLatch latch = new CountDownLatch(files.size());

        for (File file : files) {
            executor.execute( new CoffeeFileProcessor(manager, reference, file, new CompleteCallback() {
                @Override
                public void onComplete() {
                    latch.countDown();
                }
            }));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<CoffeeClass> classes = new ArrayList<CoffeeClass>(manager.getAll(CoffeeClass.class));
        Collections.sort(classes, new Comparator<CoffeeClass>() {
            @Override
            public int compare(CoffeeClass o1, CoffeeClass o2) {
                return o1.getDependencies().size() - o2.getDependencies().size();
            }
        });

        File outFile = command.outputFile;

        try {
            if (outFile.exists()) {
                if (!outFile.delete()) {
                    throw new RuntimeException("Could not delete existing file: " + outFile.getCanonicalPath());
                }
            }

            if (!outFile.createNewFile()) {
                throw new RuntimeException("Could not create file: " + outFile.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(outFile));
            writeHeader(out);
            writeIncludes(classes, FileHelper.directoryFor(outFile), command.targetPath, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("###\n");
        writer.write("#\n");
        writer.write("#  MATTBOLT.BLOGSPOT.COM\n");
        writer.write("#  Copyright(C) 2012 Matt Bolt\n");
        writer.write("#\n");
        writer.write("#  Licensed under the Apache License, Version 2.0 (the \"License\");\n");
        writer.write("#  you may not use this file except in compliance with the License.\n");
        writer.write("#  You may obtain a copy of the License at:\n");
        writer.write("#\n");
        writer.write("#      http://www.apache.org/licenses/LICENSE-2.0\n");
        writer.write("#\n");
        writer.write("#  Unless required by applicable law or agreed to in writing, software\n");
        writer.write("#  distributed under the License is distributed on an \"AS IS\" BASIS,\n");
        writer.write("#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
        writer.write("#  See the License for the specific language governing permissions and\n");
        writer.write("#  limitations under the License.\n");
        writer.write("#\n");
        writer.write("###\n");
        writer.write("\n");
        writer.write("# This file was generated with Coffee-X #\n");
        writer.write("\n");
        writer.write("include = (src) -> document.writeln \"<script type=\\\"text/javascript\\\" src=\\\"#{src}\\\"></script>\"\n");
        writer.write("\n\n");
    }

    private synchronized int incrementCompleted() {
        return completedCount.incrementAndGet();
    }

    @Override
    public GenerateCommand getCommand() {
        return command;
    }

    public CoffeeEntities getManager() {
        return manager;
    }

    public CoffeeReferenceFactory getReference() {
        return reference;
    }

    public static final class CoffeeFileProcessor implements Runnable {

        private final CoffeeFileParser parser;
        private final CompleteCallback callback;

        public CoffeeFileProcessor( CoffeeEntities manager,
                                    CoffeeReferenceFactory reference,
                                    File coffeeFile,
                                    CompleteCallback callback )
        {
            this.parser = new CoffeeFileParser(new CoffeeFileScanner(coffeeFile, reference), manager);
            this.callback = callback;
        }

        @Override
        public void run() {
            parser.parse();
            callback.onComplete();
        }
    }
    */
}
