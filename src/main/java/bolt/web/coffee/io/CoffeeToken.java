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
import bolt.web.coffee.types.CoffeeTokenType;
import bolt.web.coffee.types.CoffeeType;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * This class represents a lexical token in the Coffee Script language.
 * 
 * @author Matt Bolt
 */
public class CoffeeToken {

    private static AtomicInteger idCount = new AtomicInteger();

    private final int id;
    private final CoffeeType type;
    private final String value;
    private final CoffeeScriptLine lineNumber;

    public CoffeeToken(CoffeeType type, String value) {
        this(type, value, CoffeeScriptLine.Zero);
    }

    public CoffeeToken(CoffeeType type, String value, CoffeeScriptLine lineNumber) {
        this.id = idCount.incrementAndGet();
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoffeeToken)) return false;

        CoffeeToken that = (CoffeeToken) o;

        if (id != that.id) return false;
        if (!lineNumber.equals(that.lineNumber)) return false;
        if (!type.equals(that.type)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + type.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + lineNumber.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (getType() == CoffeeTokenType.Terminator) {
            return "[CoffeeToken - type: " + type.getType() + ", lineNumber: " + lineNumber + "]";
        }

        return "[CoffeeToken - type: " + type.getType() + ", value: " + value + ", lineNumber: " + lineNumber + "]";
    }

    public CoffeeType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public CoffeeScriptLine getLineNumber() {
        return lineNumber;
    }

}
