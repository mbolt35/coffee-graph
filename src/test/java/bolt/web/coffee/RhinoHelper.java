////////////////////////////////////////////////////////////////////////////////
//
//  MATTBOLT.BLOGSPOT.COM
//  Copyright(C) 2012 Matt Bolt
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

package bolt.web.coffee;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

/**
 * This class has test utility methods for printing rhino generate objects to standard out.
 * 
 * @author Matt Bolt
 */
public final class RhinoHelper {

    private RhinoHelper() {
        // Nope, just Chuck Testa.
    }

    public static void trace(Object object) {
        trace(object, "");
    }

    private static void trace(Object object, String spacing) {
        if (object instanceof NativeArray) {
            NativeArray arr = (NativeArray) object;
            for (Object o : arr) {
                trace(o, spacing + "  ");
            }
            return;
        }

        if (object instanceof NativeObject) {
            NativeObject nativeObj = (NativeObject) object;
            System.out.println(spacing + "Class: " + nativeObj.getClassName());
            for (Object key : nativeObj.keySet()) {
                System.out.println(spacing + "[Key: " + key + "]" );
                trace(nativeObj.get(key), spacing + "  ");
            }
            return;
        }

        if (isJavaPrimitive(object)) {
            System.out.println(spacing + object);
        } else {
            // System.out.println(spacing + ToStringBuilder.reflectionToString(object));
        }
    }

    private static boolean isJavaPrimitive(Object object) {
        return object instanceof String
            || object instanceof Integer
            || object instanceof Float
            || object instanceof Double
            || object instanceof Boolean;
    }
}
