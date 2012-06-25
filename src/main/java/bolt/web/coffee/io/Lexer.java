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

import java.io.File;
import java.util.List;

/**
 * Implementation prototype for a coffee-script tokenizer.
 *
 * @author Matt Bolt
 */
public interface Lexer {

    /**
     * This method takes a {@code File} as input and outputs a stream of {@code CoffeeToken} instances.
     *
     * @param coffeeFile The {@code File} instance to tokenize.
     *
     * @return A {@code List} of {@code CoffeeToken} instances
     */
    List<CoffeeToken> tokenize(File coffeeFile);

    /**
     * This method takes a {@code String} as input and outputs a stream of {@code CoffeeToken} instances.
     *
     * @param coffeeString The {@code String} containing coffee-script to tokenize.
     *
     * @return A {@code List} of {@code CoffeeToken} instances
     */
    List<CoffeeToken> tokenize(String coffeeString);
}
