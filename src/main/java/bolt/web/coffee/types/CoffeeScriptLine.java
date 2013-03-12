////////////////////////////////////////////////////////////////////////////////
//
//  MATTBOLT.BLOGSPOT.COM
//  Copyright(C) 2013 Matt Bolt
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at:
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package bolt.web.coffee.types;

import org.mozilla.javascript.NativeObject;

/**
 * This class represents the section of code, in line numbers, in which the token appears.
 * 
 * @author Matt Bolt
 */
public class CoffeeScriptLine {

    private static final String FirstLine = "first_line";
    private static final String FirstColumn = "first_column";
    private static final String LastLine = "last_line";
    private static final String LastColumn = "last_column";

    public static final CoffeeScriptLine Zero = new CoffeeScriptLine(0, 0, 0, 0);

    private int firstLine;
    private int firstColumn;
    private int lastLine;
    private int lastColumn;

    private CoffeeScriptLine(int firstLine, int firstColumn, int lastLine, int lastColumn) {
        this.firstLine = firstLine;
        this.firstColumn = firstColumn;
        this.lastLine = lastLine;
        this.lastColumn = lastColumn;
    }

    public int getFirstLine() {
        return firstLine;
    }

    public int getFirstColumn() {
        return firstColumn;
    }

    public int getLastLine() {
        return lastLine;
    }

    public int getLastColumn() {
        return lastColumn;
    }

    /**
     * This method will return {@code true} if the {@code Object} can be appropriately parsed into a line number.
     *
     * @param object The {@code Object} to check.
     *
     * @return {@code true} if the {@code Object} can be parsed.
     */
    public static boolean isLine(Object object) {
        if (!(object instanceof NativeObject)) {
            return false;
        }

        NativeObject nativeObject = (NativeObject) object;
        return nativeObject.containsKey(FirstLine)
            && nativeObject.containsKey(FirstColumn)
            && nativeObject.containsKey(LastLine)
            && nativeObject.containsKey(LastColumn);
    }

    /**
     * This method parses an {@code Object} into a line number.
     *
     * @param object The {@code Object} to parse.
     *
     * @return A {@code CoffeeScriptLine} parsed from the {@code Object}.
     */
    public static CoffeeScriptLine parseLine(Object object) {
        if (!(object instanceof NativeObject)) {
            return Zero;
        }

        NativeObject nativeObject = (NativeObject) object;
        return new CoffeeScriptLine(
            toInt(nativeObject.get(FirstLine)),
            toInt(nativeObject.get(FirstColumn)),
            toInt(nativeObject.get(LastLine)),
            toInt(nativeObject.get(LastColumn)));
    }

    private static int toInt(Object obj) {
        if (obj instanceof Double) {
            return ((Double) obj).intValue();
        }
        if (obj instanceof Integer) {
            return ((Integer) obj);
        }

        return 0;
    }
}
