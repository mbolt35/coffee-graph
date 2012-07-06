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
            usage();
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
