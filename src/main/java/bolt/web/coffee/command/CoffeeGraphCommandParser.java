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

package bolt.web.coffee.command;

import bolt.web.coffee.io.CoffeeGraphOptions;
import bolt.web.coffee.io.CoffeeGraphOptionsParser;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * This class parses the command line options using JCommander.
 *
 * @author Matt Bolt
 */
public class CoffeeGraphCommandParser implements CoffeeGraphOptionsParser {

    private final String[] arguments;

    private final JCommander commander;
    private final CoffeeGraphCommand command;

    private StringBuilder usageString;

    public CoffeeGraphCommandParser(String[] arguments) {
        this.arguments = arguments;

        command = new CoffeeGraphCommand();
        commander = new JCommander(command);
        commander.setProgramName("coffee-graph");
        commander.setColumnSize(100);
        commander.addConverterFactory(new FileConverterFactory());
    }

    @Override
    public CoffeeGraphOptions parse() {
        try {
            commander.parse(arguments);
        }
        catch (ParameterException e) {
            return null;
        }

        return command;
    }

    /**
     * Attempted to hide the default boolean fields by using a custom annotation and default provider with JCommander,
     * but wasn't able to. This is kind of gross, but does the job.
     */
    public void usage() {
        if (null != usageString) {
            System.out.println(usageString.toString());
            return;
        }

        final String defaultText = "Default: false";
        final int len = defaultText.length();

        usageString = new StringBuilder();
        commander.usage(usageString);

        int index = usageString.indexOf(defaultText);
        while (index != -1) {
            usageString.replace(index, index + len, "");

            index = usageString.indexOf(defaultText);
        }

        System.out.println(usageString.toString());
    }
}
