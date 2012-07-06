package bolt.web.coffee.io;

/**
 * An implementation prototype for an object which creates {@link CoffeeGraphOptions} implementations.
 *
 * @author Matt Bolt
 */
public interface CoffeeGraphOptionsParser {

    /**
     * This method parses the options parameters synchronously and returns a {@code CoffeeGraphOptions} implementation.
     */
    CoffeeGraphOptions parse();

    /**
     * This method will print any usage or option information to standard out.
     */
    void usage();

}
