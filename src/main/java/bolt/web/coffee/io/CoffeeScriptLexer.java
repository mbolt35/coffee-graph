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

package bolt.web.coffee.io;

import bolt.web.coffee.types.CoffeeScriptLine;
import bolt.web.coffee.types.CoffeeScriptType;
import bolt.web.coffee.types.CoffeeType;
import bolt.web.coffee.types.UndefinedCoffeeType;
import bolt.web.coffee.util.CoffeeScript;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class uses a {@code CoffeeScript} instance, which uses rhino to execute the javascript coffee-script utilities.
 * 
 * @author Matt Bolt
 */
public class CoffeeScriptLexer implements Lexer {

    private final CoffeeScript coffeeScript;

    public CoffeeScriptLexer() {
        coffeeScript = new CoffeeScript();
    }

    @Override
    public List<CoffeeToken> tokenize(File coffeeFile) {
        return tokenize(coffeeScript.tokenize(coffeeFile));
    }

    @Override
    public List<CoffeeToken> tokenize(String coffeeString) {
        return tokenize(coffeeScript.tokenize(coffeeString));
    }

    @SuppressWarnings({"unchecked"})
    private List<CoffeeToken> tokenize(NativeArray nativeTokens) {
        List<CoffeeToken> tokens = new ArrayList<CoffeeToken>();

        ArrayList<NativeArray> list = new ArrayList<NativeArray>(nativeTokens);
        for (NativeArray token : list) {
            tokens.add( tokenFrom(new ArrayList<Object>(token)) );
        }

        return tokens;
    }

    private CoffeeToken tokenFrom(List<Object> tokenEntries) {
        String typeString = (String) tokenEntries.get(0);
        CoffeeType type = CoffeeScriptType.typeFor(typeString);

        if (type == CoffeeScriptType.NoType) {
            // To keep the process going, let's create a "filler" coffee type, and have it report a warning
            type = new UndefinedCoffeeType(typeString);
        }

        Object next = tokenEntries.size() > 1 ? tokenEntries.get(1) : "";

        if (CoffeeScriptLine.isLine(next)) {
            return new CoffeeToken(type, type.getType(), CoffeeScriptLine.parseLine(next));
        }

        NativeObject obj = tokenEntries.size() > 2 ? (NativeObject) tokenEntries.get(2) : null;

        return new CoffeeToken(type, next.toString(), CoffeeScriptLine.parseLine(obj));
    }
}
