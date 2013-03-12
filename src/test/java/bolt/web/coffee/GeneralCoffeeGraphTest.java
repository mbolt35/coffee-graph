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
import bolt.web.coffee.dependency.graph.CyclicDependencyException;
import bolt.web.coffee.io.CoffeeScriptLexer;
import bolt.web.coffee.io.CoffeeScriptMinParser;
import bolt.web.coffee.io.exporters.ListFilesExporter;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the basic functionality of coffee-graph using an asserting exporter.
 *
 * @author Matt Bolt
 */
public class GeneralCoffeeGraphTest extends BaseTestCase {

    @Test
    public void simpleTest() throws Exception {
        List<File> files = Collections.singletonList( classPathFile("/simple-test") );

        buildDependencies()
            .withTokensFrom(new CoffeeScriptLexer())
            .parsedWith(new CoffeeScriptMinParser())
            .exportedBy(new AssertingExporter(new AssertExportOrder() {
                @Override
                public void assertOrder(List<CoffeeIdentifier> identifiers) throws Exception {
                    assertEquals(identifiers.get(0).getFile().getName(), "Foo.coffee");
                    assertEquals(identifiers.get(1).getFile().getName(), "Bar.coffee");
                }
            }))
            .build(files);
    }

    @Test
    public void innerScopeIdentifierOverrideTest() throws Exception {
        List<File> files = Collections.singletonList( classPathFile("/id-test") );

        buildDependencies()
            .withTokensFrom(new CoffeeScriptLexer())
            .parsedWith(new CoffeeScriptMinParser())
            .exportedBy(new AssertingExporter(new AssertExportOrder() {
                @Override
                public void assertOrder(List<CoffeeIdentifier> identifiers) throws Exception {
                    List<String> fileNames = toFileNames(identifiers);

                    assertTrue( fileNames.indexOf("Roo.coffee") < fileNames.indexOf("Bar.coffee") );
                }
            }))
            .build(files);
    }

    @Test
    public void cyclicDependencyTest() throws Exception {
        List<File> files = Collections.singletonList( classPathFile("/cyclic") );

        try {
            buildDependencies()
                .withTokensFrom(new CoffeeScriptLexer())
                .parsedWith(new CoffeeScriptMinParser())
                .exportedBy(new ListFilesExporter())
                .build(files);
        }
        catch (CyclicDependencyException e) {
            return;
        }
        catch (Exception e) {
            throw new Exception("Did not catch Cyclic Dependency", e);
        }

        throw new Exception("Did not catch Cyclic Dependency");
    }

    @Test
    public void multipleDirectoryTest() throws Exception {
        List<File> files = Collections.singletonList(classPathFile("/multiple-directories") );

        buildDependencies()
            .withTokensFrom(new CoffeeScriptLexer())
            .parsedWith(new CoffeeScriptMinParser())
            .exportedBy(new ListFilesExporter())
            .exportedBy(new AssertingExporter(new AssertExportOrder() {
                @Override
                public void assertOrder(List<CoffeeIdentifier> identifiers) throws Exception {
                    List<String> fileNames = toFileNames(identifiers);

                    int aIndex = fileNames.indexOf("A.coffee");
                    int bIndex = fileNames.indexOf("B.coffee");
                    int cIndex = fileNames.indexOf("C.coffee");

                    assertTrue( bIndex < aIndex && bIndex < cIndex );
                }
            }))
            .build(files);
    }

    @Test
    public void reassignThisTest() throws Exception {
        List<File> files = Collections.singletonList(classPathFile("/reassign"));

        buildDependencies()
            .withTokensFrom(new CoffeeScriptLexer())
            .parsedWith(new CoffeeScriptMinParser())
            .exportedBy(new AssertingExporter(new AssertExportOrder() {
                @Override
                public void assertOrder(List<CoffeeIdentifier> identifiers) throws Exception {
                    List<String> fileNames = toFileNames(identifiers);

                    for (String fileName : fileNames) {
                        System.out.println("File: " + fileName);
                    }

                    assertEquals(fileNames.get(0), "Foo.coffee");
                    assertEquals(fileNames.get(1), "Hoo.coffee");
                    assertEquals(fileNames.get(2), "Bar.coffee");
                    assertEquals(fileNames.get(3), "Woo.coffee");
                }
            }, true))
            .build(files);
    }

    @Test
    public void windowTest() throws Exception {
        List<File> files = Collections.singletonList(classPathFile("/window"));

        buildDependencies()
            .withTokensFrom(new CoffeeScriptLexer())
            .parsedWith(new CoffeeScriptMinParser())
            .exportedBy(new AssertingExporter(new AssertExportOrder() {
                @Override
                public void assertOrder(List<CoffeeIdentifier> identifiers) throws Exception {
                    List<String> fileNames = toFileNames(identifiers);

                    for (String fileName : fileNames) {
                        System.out.println(fileName);
                    }

                    assertEquals(fileNames.get(0), "B.coffee");
                    assertEquals(fileNames.get(1), "A.coffee");
                }
            }))
            .build(files);
    }

    @Test
    public void abcDemo() throws Exception {
        List<File> files = Collections.singletonList(classPathFile("/demo"));

        buildDependencies()
            .withTokensFrom(new CoffeeScriptLexer())
            .parsedWith(new CoffeeScriptMinParser())
            .exportedBy(new AssertingExporter(new AssertExportOrder() {
                @Override
                public void assertOrder(List<CoffeeIdentifier> identifiers) throws Exception {
                    List<String> fileNames = toFileNames(identifiers);

                    int aIndex = fileNames.indexOf("A.coffee");
                    int bIndex = fileNames.indexOf("B.coffee");
                    int cIndex = fileNames.indexOf("C.coffee");

                    assertTrue( bIndex < aIndex && aIndex < cIndex );
                }
            }))
            .build(files);
    }

}
