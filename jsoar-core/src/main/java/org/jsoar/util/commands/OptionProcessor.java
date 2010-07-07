/*
 * Copyright (c) 2010  Jonathan Voigt <voigtjr@gmail.com>
 *
 * Created on June 29, 2010
 */
package org.jsoar.util.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoar.kernel.SoarException;

/**
 * <p>Option processor utility for Soar command line interface. Very similar to
 * csoar option processor. Intended usage: create, register options, loop:
 * process lines, has, getArgument. Each call to process lines resets the
 * internal state but does not unregister options.
 * 
 * <p>Long options should be all lower-case letters and dashes. Short options
 * should be a single upper- or lower- case letter.
 * 
 * <p>TODO: SoarException probably the wrong exception to use since this has
 * nothing (really) to do with Soar
 * 
 * <p>TODO: partial matches on long options
 * 
 * <p>TODO: potentially rewrite using buffers or something to avoid so many string
 * objects
 * 
 * @author voigtjr
 * 
 * @param <E>
 *            user-defined option reference object
 */
public class OptionProcessor<E>
{
    /**
     * <p>Factory method.
     * 
     * @param <E>
     *            user-defined option reference object
     * @return New option processor instance.
     */
    public static <E> OptionProcessor<E> create()
    {
        return new OptionProcessor<E>();
    }

    public class OptionBuilder
    {
        private final E key;

        private final String longOption;

        private char shortOption;

        private ArgType type = ArgType.NONE;

        private boolean registered = false;

        /**
         * <p>Starts building a new option. Call using newOption(). Must call
         * register() before this option will work.
         * 
         * <p>By default the short option will be the first character of the long
         * option. To make a short option with a capital letter, capitalize the
         * first character of the long option, or call setShortOption later. The
         * long option will be forced to lower-case after setting the short
         * option.
         * 
         * @param key
         *            User key to refer to this option later.
         * @param longOption
         *            Long option, omit initial dashes, will be forced to
         *            lower-case after first char is used for short option.
         * @throws NullPointerException
         *             If any arguments are null.
         * @throws IllegalArgumentException
         *             If arguments have bad content.
         */
        private OptionBuilder(E key, String longOption)
        {
            if (key == null)
                throw new NullPointerException("key must not be null.");

            if (longOption == null)
                throw new NullPointerException("longOption must not be null.");

            if (longOption.isEmpty())
                throw new IllegalArgumentException(
                        "Long option is empty string.");

            if (longOption.matches("[^a-z-]+"))
                throw new IllegalArgumentException(
                        "Illegal characters in long option.");

            if (longOption.charAt(0) == '-')
                throw new IllegalArgumentException(
                        "Long option can't start with dash.");

            this.key = key;
            shortOption(longOption.charAt(0));

            longOption = longOption.toLowerCase();
            this.longOption = longOption;
        }

        /**
         * <p>Set the short option. The default for this is the first character of
         * the long option. Must be a letter.
         * 
         * @param shortOption
         * @return The builder.
         * @throws IllegalArgumentException
         *             If argument has bad content.
         * @throws IllegalStateException
         *             If option is already registered.
         */
        public OptionBuilder shortOption(char shortOption)
        {
            if (registered)
                throw new IllegalStateException("Already registered.");

            if (!Character.isLetter(shortOption))
                throw new IllegalArgumentException(
                        "Short option is not a single letter.");
            this.shortOption = shortOption;
            return this;
        }

        /**
         * <p>Mark this option as having no argument. This is the default.
         * 
         * @return The builder.
         * @throws IllegalStateException
         *             If option is already registered.
         */
        public OptionBuilder noArg()
        {
            if (registered)
                throw new IllegalStateException("Already registered.");

            type = ArgType.NONE;
            return this;
        }

        /**
         * <p>Mark this option as having an optional argument.
         * 
         * <p>This option will consume the next option on the line, if any. When
         * using short options, the argument can be placed with the short
         * option: -aone could be equivalent to -a one
         * 
         * @return The builder.
         * @throws IllegalStateException
         *             If option is already registered.
         */
        public OptionBuilder optionalArg()
        {
            if (registered)
                throw new IllegalStateException("Already registered.");

            type = ArgType.OPTIONAL;
            return this;
        }

        /**
         * <p>Mark this option as having a required argument. This option must be
         * followed by an argument.
         * 
         * <p>This option will consume the next option on the line, if any. When
         * using short options, the argument can be placed with the short
         * option: -aone could be equivalent to -a one
         * 
         * @return The builder.
         * @throws IllegalStateException
         *             If option is already registered.
         */
        public OptionBuilder requiredArg()
        {
            if (registered)
                throw new IllegalStateException("Already registered.");

            type = ArgType.REQUIRED;
            return this;
        }

        /**
         * <p>Register the option with the option manager.
         * 
         * @throws IllegalStateException
         *             If option is already registered.
         * @throws IllegalArgumentException
         *             If there is another option with the short or long option
         *             name already registered.
         */
        public void register()
        {
            if (registered)
                throw new IllegalStateException("Already registered.");

            arguments = null;

            Option prev = shortOptions.put(shortOption, new Option(key, type));
            if (prev != null)
                throw new IllegalArgumentException(
                        "Already have a short option using -" + shortOption
                                + ": " + prev.key);
            prev = longOptions.put(longOption, new Option(key, type));
            if (prev != null)
            {
                shortOptions.remove(shortOption);
                throw new IllegalArgumentException(
                        "Already have a long option using --" + longOption
                                + ": " + prev.key);
            }
            registered = true;
        }
    }

    /**
     * <p>Create a new option builder associating the passed key with the passed
     * long option. Call register() on returned builder object to register it
     * with the associated option processor. The short option defaults to the
     * first letter of the long option. To use an upper-case letter in the short
     * option, pass a long option with an upper-case first letter--the long
     * option will be forced to lower-case after.
     * 
     * <p>Set a custom short option by calling setShortOption on the returned
     * builder. Useful for commands with more than two long options that start
     * with the same letter.
     * 
     * <p>Argument type defaults to none, call setOptionalArg or setRequiredArg to
     * change.
     * 
     * @param key
     *            User-supplied and user-defined index for this option and its
     *            argument in the future.
     * @param longOption
     *            The long option for this argument.
     * @return Option builder, call register() on this to complete registration
     *         of command.
     * @throws NullPointerException
     *             If any arguments are null.
     * @throws IllegalArgumentException
     *             If arguments have bad content.
     */
    public OptionBuilder newOption(E key, String longOption)
    {
        return new OptionBuilder(key, longOption);
    }

    /**
     * <p>Argument type, denotes whether or not the option takes an argument or
     * not, or if it is optional.
     * 
     * @author voigtjr
     * 
     */
    private enum ArgType
    {
        NONE, REQUIRED, OPTIONAL,
    }

    private class Option
    {
        Option(E key, ArgType type)
        {
            this.key = key;
            this.type = type;
        }

        E key;

        ArgType type;

        @Override
        public String toString()
        {
            return key.toString() + "(" + type.toString() + ")";
        }
    }

    private final Map<Character, Option> shortOptions = new HashMap<Character, Option>();

    private final Map<String, Option> longOptions = new HashMap<String, Option>();

    private Map<E, String> arguments = new HashMap<E, String>();

    /**
     * <p>Evaluate a command line. Assumes first arg is command name (ignored).
     * 
     * <p>Short options are preceded by one dash and may be combined together.
     * "-fo" is equivalent to "-f -o". If an option takes an argument, the rest
     * of the token is checked and used before checking for and using the next
     * argument. For example, if "-f" takes an argument, "-fo" means "o" is the
     * argument for "-f" and is equivalent to "-f o"
     * 
     * <p>Long options are preceded by two dashes and must be by themselves. The
     * next argument is used for the option argument if the option takes an
     * argument.
     * 
     * <p>Optional arguments are tricky and could be implemented a few ways. This
     * implementation assumes that if there is any argument following an
     * argument with an optional argument, that it is that option's argument.
     * For example, if --foo takes an optional argument, then "--foo --bar" will
     * mean that "--bar" is interpreted as an argument to the "--foo" option,
     * and not its own option.
     * 
     * <p>Non-option arguments are collected and returned in a new list in the same
     * order they are encountered.
     * 
     * @param args
     *            Command line, first arg is command name (ignored).
     * @return Only non-option arguments in same order as on the original
     *         command line.
     * @throws SoarException
     *             If there is an unknown option on the command line, or a
     *             missing required argument.
     */
    public List<String> process(List<String> args) throws SoarException
    {
        arguments = new HashMap<E, String>();

        List<String> nonOptionArgs = new ArrayList<String>();

        if (args.isEmpty())
            return nonOptionArgs;

        Iterator<String> iter = args.iterator();
        // assume first argument is command name
        iter.next();
        boolean done = false;
        while (iter.hasNext())
        {
            String arg = iter.next();

            if (!done && arg.length() > 1 && arg.charAt(0) == '-')
            {
                if (arg.charAt(1) == '-')
                {
                    if (arg.length() == 2)
                    {
                        // stop processing args
                        done = true;
                    }
                    else
                    {
                        Option option = resolveLongOption(arg.substring(2));
                        if (option == null)
                            throw new SoarException("Unknown option: " + arg);

                        processOption(option, arg, iter);
                    }
                    continue;
                }
                else
                {
                    if (!isNumber(arg))
                    {
                        for (int i = 1; i < arg.length(); ++i)
                        {
                            Option option = resolveShortOption(arg.charAt(i));
                            if (option == null)
                                throw new SoarException("Unknown option: "
                                        + arg);

                            boolean consumedArg = processOption(option, arg,
                                    iter, i);
                            if (consumedArg)
                                break;
                        }
                        continue;
                    }
                }
            }
            nonOptionArgs.add(arg);
        }

        return nonOptionArgs;
    }

    private boolean processOption(Option option, String arg,
            Iterator<String> iter) throws SoarException
    {
        return processOption(option, arg, iter, -1);
    }

    private boolean processOption(Option option, String arg,
            Iterator<String> iter, int at) throws SoarException
    {
        boolean consumedArg = false;
        if (option.type == ArgType.NONE)
            arguments.put(option.key, null);
        else
        {
            consumedArg = true;

            // This block of code here allows arguments appended directly to
            // options
            String optArg = null;
            if (at >= 0)
            {
                if (at + 1 < arg.length())
                    optArg = arg.substring(at + 1);
            }
            if (optArg == null)
            {
                optArg = iter.hasNext() ? iter.next() : null;
            }

            if (option.type == ArgType.REQUIRED)
                if (optArg == null)
                    throw new SoarException("Option requires argument: " + arg);

            arguments.put(option.key, optArg);
        }
        return consumedArg;
    }

    private boolean isNumber(String arg)
    {
        try
        {
            Integer.parseInt(arg);
            return true;
        }
        catch (NumberFormatException e)
        {
        }
        try
        {
            Double.parseDouble(arg);
            return true;
        }
        catch (NumberFormatException e)
        {
        }
        return false;
    }

    private Option resolveLongOption(String arg)
    {
        return longOptions.get(arg.toLowerCase());
    }

    private Option resolveShortOption(char arg) throws SoarException
    {
        if (!Character.isLetter(arg))
            throw new SoarException("Short option is not a letter: "
                    + arg);
        return shortOptions.get(arg);
    }

    /**
     * <p>Test to see if the most recent invocation of process uncovered the given
     * option.
     * 
     * @param key
     *            Key for option to test.
     * @return True if it was successfully encountered.
     * @throws IllegalStateException
     *             If process() not called between registering options and
     *             calling this function.
     * @throws NullPointerException
     *             If option is null.
     */
    public boolean has(E key)
    {
        if (key == null)
            throw new NullPointerException("Key is null.");
        if (arguments == null)
            throw new IllegalStateException(
                    "Call process() before testing for options.");
        return arguments.containsKey(key);
    }

    /**
     * <p>Manually set an option as if it had been encountered during process.
     * 
     * Caution: This doesn't enforce optional/required arguments!
     * 
     * @param key
     *            The key for the option to set.
     * @throws IllegalStateException
     *             If process() not called between registering options and
     *             calling this function.
     */
    public void set(E key)
    {
        set(key, null);
    }

    /**
     * <p>Manually unset an option as if it had never been encountered during
     * process.
     * 
     * @param key
     *            The key for the option to unset.
     * @throws IllegalStateException
     *             If process() not called between registering options and
     *             calling this function.
     * @throws NullPointerException
     *             If option is null.
     */
    public void unset(E key)
    {
        if (key == null)
            throw new NullPointerException("Key is null.");
        if (arguments == null)
            throw new IllegalStateException(
                    "Call process() before testing for options.");
        arguments.remove(key);
    }

    /**
     * <p>Manually set an option and its argument.
     * 
     * <p>Caution: This doesn't enforce optional/required arguments!
     * 
     * @param key
     *            The key for the option to set.
     * @param argument
     *            The option's argument, or null
     * @throws IllegalStateException
     *             If process() not called between registering options and
     *             calling this function.
     * @throws NullPointerException
     *             If option is null.
     */
    public void set(E key, String argument)
    {
        if (key == null)
            throw new NullPointerException("Key is null.");
        if (arguments == null)
            throw new IllegalStateException(
                    "Call process() before testing for options.");
        arguments.put(key, argument);
    }

    /**
     * <p>Get an option's argument as a String.
     * 
     * @param key
     *            Key for option who's argument needs retrieval
     * @return The option's argument as a string.
     * @throws IllegalStateException
     *             If process() not called between registering options and
     *             calling this function.
     * @throws NullPointerException
     *             If option is null.
     */
    public String get(E key)
    {
        if (key == null)
            throw new NullPointerException("Key is null.");
        if (arguments == null)
            throw new IllegalStateException(
                    "Call process() before testing for options.");
        return arguments.get(key);
    }
    
    /**
     * <p>Get an option's argument as an integer.
     * 
     * @param key
     *            Key for option who's argument needs retrieval
     * @return The option's argument as a string.
     * @throws IllegalStateException
     *             If process() not called between registering options and
     *             calling this function.
     * @throws NullPointerException
     *             If option is null.
     * @throws SoarException
     *             If argument is not an integer.
     */
    public int getInteger(E key) throws SoarException
    {
        String arg = get(key);
        try
        {
            return Integer.parseInt(arg);
        }
        catch(NumberFormatException e)
        {
            throw new SoarException("Invalid integer value: " + arg);
        }
    }

    /**
     * <p>Get an option's argument as a double.
     * 
     * @param key
     *            Key for option who's argument needs retrieval
     * @return The option's argument as a string.
     * @throws IllegalStateException
     *             If process() not called between registering options and
     *             calling this function.
     * @throws NullPointerException
     *             If option is null.
     * @throws SoarException
     *             If argument is not a double.
     */
    public double getDouble(E key) throws SoarException
    {
        String arg = get(key);
        try
        {
            return Double.parseDouble(arg);
        }
        catch(NumberFormatException e)
        {
            throw new SoarException("Invalid double value: " + arg);
        }
    }

    /**
     * <p>Get an option's argument as a float.
     * 
     * @param key
     *            Key for option who's argument needs retrieval
     * @return The option's argument as a string.
     * @throws IllegalStateException
     *             If process() not called between registering options and
     *             calling this function.
     * @throws NullPointerException
     *             If option is null.
     * @throws SoarException
     *             If argument is not a float.
     */
    public float getFloat(E key) throws SoarException
    {
        String arg = get(key);
        try
        {
            return Float.parseFloat(arg);
        }
        catch(NumberFormatException e)
        {
            throw new SoarException("Invalid float value: " + arg);
        }
    }
}
