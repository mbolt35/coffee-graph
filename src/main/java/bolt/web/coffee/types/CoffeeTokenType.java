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

package bolt.web.coffee.types;

import java.util.HashMap;
import java.util.Map;

/**
 * This enumeration contains token types emitted from the coffee script lexer.
 *
 * @author Matt Bolt
 */
public enum CoffeeTokenType implements CoffeeType {
    Null("NULL"),
    Undefined("UNDEFINED"),
    Terminator("TERMINATOR"),
    Class("CLASS"),
    Extends("EXTENDS"),
    Super("SUPER"),
    This("THIS"),
    Indent("INDENT"),
    Outdent("OUTDENT"),
    Identifier("IDENTIFIER"),
    ParamStart("PARAM_START"),
    ParamEnd("PARAM_END"),
    CallStart("CALL_START"),
    CallEnd("CALL_END"),
    String("STRING"),
    Return("RETURN"),
    IndexStart("INDEX_START"),
    IndexEnd("INDEX_END"),
    Number("NUMBER"),
    Unary("UNARY"),
    If("IF"),
    Else("ELSE"),
    PostIf("POST_IF"),
    HereComment("HERECOMMENT"),
    For("FOR"),
    ForIn("FORIN"),
    ForOf("FOROF"),
    When("WHEN"),
    Until("UNTIL"),
    Compare("=="),
    CompoundAssign("COMPOUND_ASSIGN"),
    Logic("LOGIC"),
    While("WHILE"),
    IndexSoak("INDEX_SOAK"),
    LeadingWhen("LEADING_WHEN"),
    Shift("SHIFT"),
    Regex("REGEX"),
    Statement("STATEMENT"),
    Math("MATH"),
    Own("OWN"),
    Loop("LOOP"),
    By("BY"),
    FunctionExists("FUNC_EXIST"),
    Switch("SWITCH"),
    JavaScript("JS"),
    Bool("BOOL"),
    Relation("RELATION"),
    Try("TRY"),
    Catch("CATCH"),
    Throw("THROW"),
    Finally("FINALLY");

    private final java.lang.String type;

    CoffeeTokenType(java.lang.String type) {
        this.type = type;
    }

    public java.lang.String getType() {
        return type;
    }

    private static final Map<String, CoffeeTokenType> byTypeValue = new HashMap<String, CoffeeTokenType>();

    /**
     * This method performs a lookup of the {@code CoffeeTokenType} using the {@code type} argument provided. Note that
     * this is *not* the same as {@code valueOf()} - This method looks up the {@code CoffeeTokenType} based on the
     * internal type string.
     *
     * <p>This method also lazily initializes a {@code Map} for fast look-ups.</p>
     *
     * @param type The {@code String} type value to lookup the {@code CoffeeTokenType} for.
     *
     * @return
     */
    static CoffeeType typeFor(java.lang.String type) {
        if (byTypeValue.isEmpty()) {
            for (CoffeeTokenType tokenType : CoffeeTokenType.values()) {
                byTypeValue.put(tokenType.getType(), tokenType);
            }
        }

        if (null == type) {
            throw new NullPointerException();
        }

        if (!byTypeValue.containsKey(type)) {
            throw new IllegalArgumentException(type);
        }

        return byTypeValue.get(type);
    }

}
