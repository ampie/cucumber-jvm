package cucumber.runtime;

import gherkin.formatter.*;
import gherkin.formatter.Formatter;
import gherkin.formatter.model.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/*
 * TODO NOTE!!!
 * This class is a shameless hack of gherkin.formatter.FilterFormatter. I could not find a way to extend its behavior
 * The single modification is in the "detectFilter" method. Please let me know if I can improve this
 * Ampie Barnard
 */
public class CustomFilterFormatter extends FilterFormatter {
    public CustomFilterFormatter(Formatter formatter, List filters) {
        super(formatter, Arrays.asList("@tag1"));
        try {
            //NB!!! This only works because "filter" is initialized in the constructor
            Field filter = FilterFormatter.class.getDeclaredField("filter");
            filter.setAccessible(true);
            filter.set(this, detectFilter(filters));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new CucumberException(e.getMessage(),e);
        }
    }

    private Filter detectFilter(List filters) {
        Set<Class> filterClasses = new HashSet<Class>();
        for (Object filter : filters) {
            filterClasses.add(filter.getClass());
        }
        if (filterClasses.size() > 1) {
            throw new IllegalArgumentException("Inconsistent filters: " + filters + ". Only one type [line,name,tag] can be used at once.");
        }

        Class<?> typeOfFilter = filters.get(0).getClass();
        if (String.class.isAssignableFrom(typeOfFilter)) {
            return new TagFilter(filters);
        } else if (Number.class.isAssignableFrom(typeOfFilter)) {
            return new LineFilter(filters);
        } else if (Pattern.class.isAssignableFrom(typeOfFilter)) {
            return new PatternFilter(filters);
            //MODIFICATION STARTS - Ampie
        } else if (CustomFilterConfig.class.isAssignableFrom(typeOfFilter)) {
            return new CustomizableFilter(filters);
            //MODIFICATION ENDS - Ampie
        } else {
            throw new RuntimeException("Could not create filter method for unknown filter of type: " + typeOfFilter);
        }
    }
}
