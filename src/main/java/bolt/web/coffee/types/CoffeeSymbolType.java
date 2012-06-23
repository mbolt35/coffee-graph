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
 * This enumeration contains types of symbols which are emitted from the tokenizer. From what I can tell, these are
 * the one-to-one symbols. In other words, while they may be defined as a "type" in the lexer, their purpose is more
 * explicit, whereas the typical "type" rules may not apply.
 *
 * <p>Some of these symbols may overlap with token types. It seems that if the symbols are optional in coffee-script,
 * they're simply passed out as the type.</p>
 *
 * @author Matt Bolt
 */
public enum CoffeeSymbolType implements CoffeeType {
    At("@"),
    This("this"),
    Document("document"),
    Window("window"),
    Dot("."),
    Colon(":"),
    Lambda("->"),
    BindingLambda("=>"),
    Splat("..."),
    OpenParens("("),
    CloseParens(")"),
    OpenArray("["),
    CloseArray("]"),
    OpenObject("{"),
    CloseObject("}"),
    Comma(","),
    Existential("?"),
    ExistentialField("?."),
    Increment("++"),
    Decrement("--"),
    Range(".."),
    Plus("+"),
    Minus("-"),
    Division("/"),
    Multiply("*"),
    Assignment("="),
    Accessor("::");

    private final String type;

    CoffeeSymbolType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private static final Map<String, CoffeeSymbolType> byTypeValue = new HashMap<String, CoffeeSymbolType>();

    /**
     * This method performs a lookup of the {@code CoffeeSymbolType} using the {@code type} argument provided. Note
     * that this is *not* the same as {@code valueOf()} - This method looks up the {@code CoffeeSymbolType} based on the
     * internal type string.
     *
     * <p>This method also lazily initializes a {@code Map} for fast look-ups.</p>
     *
     * @param symbol The {@code String} type value to lookup the {@code CoffeeSymbolType} for.
     *
     * @return
     */
    static CoffeeType typeFor(String symbol) {
        if (byTypeValue.isEmpty()) {
            for (CoffeeSymbolType symbolType : CoffeeSymbolType.values()) {
                byTypeValue.put(symbolType.getType(), symbolType);
            }
        }

        if (null == symbol) {
            throw new NullPointerException();
        }

        if (!byTypeValue.containsKey(symbol)) {
            throw new IllegalArgumentException(symbol);
        }

        return byTypeValue.get(symbol);
    }
}
