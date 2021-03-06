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

package bolt.web.coffee.util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class uses Mozilla's Rhino javascript engine to wrap the coffee-script JS compiler. The latest coffee-script
 * release (relative to this version of coffee-graph) is located on the classpath; however, there is also an option
 * for specifying a custom coffee-script js compiler via command line.
 *
 * <p>
 * Special thanks to David Yeung, the author of JCoffeeScript, for the rhino leveraging inspiration. You saved me many
 * hours and failed attempts at reproducing the coffee-script grammar. JCoffeeScript can be found on github here:
 * <a href="https://github.com/yeungda/jcoffeescript">JCoffeeScript GitHub</a>
 *
 * @author Matt Bolt
 */
public class CoffeeScript {

    public static final String COFFEE_JS = "https://raw.github.com/jashkenas/coffee-script/master/extras/coffee-script.js";

    private static final String COMPILE = "CoffeeScript.compile(coffeeFile, { bare: %s });";
    private static final String TOKENIZE = "CoffeeScript.tokens(coffeeFile, { bare: true });";
    private static final String PARSE = "CoffeeScript.nodes(coffeeFile, { bare: true });";
    private static final String VERSION = "CoffeeScript.VERSION";

    private final Scriptable javascript;

    public CoffeeScript() {
        this.javascript = initialize("CoffeeScript.js");
    }

    public CoffeeScript(String url)  {
        try {
            this.javascript = initialize(new URL(url));
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

    private Scriptable initialize(String classpathResource) {
        ClassLoader loader = getClass().getClassLoader();
        InputStream stream = loader.getResourceAsStream(classpathResource);

        return initialize(stream);
    }

    private Scriptable initialize(URL url) {
        try {
            InputStream stream = url.openStream();

            return initialize(stream);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private Scriptable initialize(InputStream stream) {
        StringWriter writer = new StringWriter();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            try {
                String line = reader.readLine();
                while (line != null) {
                    writer.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (Exception e) {
                throw new Error(e);
            } finally {
                reader.close();
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scriptable coffeeScriptable = null;
        String sourceText = writer.toString();

        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        try {
            coffeeScriptable = context.initStandardObjects();
            context.evaluateString(coffeeScriptable, sourceText, "CoffeeScript", 0, null);
        } finally {
            Context.exit();
        }

        return coffeeScriptable;
    }

    public NativeArray tokenize(File coffeeFile) {
        return tokenize(FileHelper.loadText(coffeeFile));
    }

    public NativeArray tokenize(String coffeeSource) {
        Context context = Context.enter();
        Scriptable lexScope = context.newObject(javascript);
        lexScope.setParentScope(javascript);
        lexScope.put("coffeeFile", lexScope, coffeeSource);

        try {
            return (NativeArray) context.evaluateString(lexScope, TOKENIZE, "CoffeeScript", 0, null);
        } finally {
            Context.exit();
        }
    }

    public String compile(File coffeeFile, boolean bare) {
        return compile(FileHelper.loadText(coffeeFile), bare);
    }

    public String compile(String coffeeSource, boolean bare) {
        Context context = Context.enter();
        Scriptable compileScope = context.newObject(javascript);
        compileScope.setParentScope(javascript);
        compileScope.put("coffeeFile", compileScope, coffeeSource);

        String compileCommand = String.format(COMPILE, String.valueOf(bare));

        try {
            return (String) context.evaluateString(compileScope, compileCommand, "CoffeeScript", 0, null);
        } finally {
            Context.exit();
        }
    }

    public NativeObject parse(File coffeeFile) {
        return parse(FileHelper.loadText(coffeeFile));
    }

    public NativeObject parse(String coffeeSource) {
        Context context = Context.enter();
        Scriptable parseScope = context.newObject(javascript);
        parseScope.setParentScope(javascript);
        parseScope.put("coffeeFile", parseScope, coffeeSource);

        try {
            return (NativeObject) context.evaluateString(parseScope, PARSE, "CoffeeScript", 0, null);
        } finally {
            Context.exit();
        }
    }

    /**
     * This method returns the current version of the CoffeeScript compiler used.
     *
     * @return A {@code String} representation of the CoffeeScript version.
     */
    public String getVersion() {
        Context context = Context.enter();
        Scriptable parseScope = context.newObject(javascript);
        parseScope.setParentScope(javascript);

        try {
            return (String) context.evaluateString(parseScope, VERSION, "CoffeeScript", 0, null);
        } finally {
            Context.exit();
        }
    }
}
