package cucumber.runtime;

import gherkin.formatter.Filter;
import gherkin.formatter.model.ExamplesTableRow;
import gherkin.formatter.model.Range;
import gherkin.formatter.model.Tag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CustomizableFilter implements Filter {
    List<Filter> filters = new ArrayList<Filter>();

    public CustomizableFilter(List<CustomFilterConfig> filters) {
        for (CustomFilterConfig filterConfig : filters) {
            this.filters.add(buildFilter(filterConfig));
        }
    }

    private Filter buildFilter(CustomFilterConfig filterConfig) {
        Constructor<? extends Filter> constructor = null;
        for (Constructor<?> potentialConstructor : filterConfig.getFilterClass().getConstructors()) {
            if (potentialConstructor.getParameterTypes().length == 1 && List.class.isAssignableFrom(potentialConstructor.getParameterTypes()[0])) {
                constructor = (Constructor<? extends Filter>) potentialConstructor;
                break;
            } else if (potentialConstructor.getParameterTypes().length == 0) {
                constructor = (Constructor<? extends Filter>) potentialConstructor;
            }
        }
        if (constructor == null) {
            throw new CucumberException("Could not find a suitable constructor for custom filter class "
                    + filterConfig.getFilterClass() + ". It requires either a constructor that takes a List " +
                    "of Strings, or an empty constructor");

        }
        return instantiate(filterConfig, constructor);
    }

    private Filter instantiate(CustomFilterConfig filterConfig, Constructor<? extends Filter> constructor) {
        try {
            if (constructor.getParameterTypes().length == 0) {
                return constructor.newInstance();
            } else {
                return constructor.newInstance(filterConfig.getFilterParams());
            }
        } catch (Exception e) {
            throw new CucumberException("Could not instantiate filter", e.getCause() == null?e:e.getCause());
        }
    }

    @Override
    public boolean evaluate(List<Tag> tags, List<String> names, List<Range> ranges) {
        for (Filter filter : filters) {
            if (!filter.evaluate(tags, names, ranges)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<ExamplesTableRow> filterTableBodyRows(List<ExamplesTableRow> examplesRows) {
        List<ExamplesTableRow> result = new ArrayList<ExamplesTableRow>(examplesRows);
        for (Filter filter : filters) {
            result = filter.filterTableBodyRows(result);
        }
        return result;
    }
}
