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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTestCase {

    protected File classPathFile(String name) throws Exception {
        return new File(getClass().getResource(name).toURI());
    }

    protected static List<String> toFileNames(List<CoffeeIdentifier> identifiers) {
        List<String> result = new ArrayList<String>(identifiers.size());
        for (CoffeeIdentifier identifier : identifiers) {
            result.add(identifier.getFile().getName());
        }
        return result;
    }

    protected static CoffeeScriptDependencyBuilder buildDependencies() {
        return new CoffeeScriptDependencyBuilder();
    }
}
