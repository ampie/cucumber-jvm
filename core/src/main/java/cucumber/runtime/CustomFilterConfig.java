package cucumber.runtime;

import cucumber.api.Transformer;
import cucumber.deps.com.thoughtworks.xstream.converters.SingleValueConverter;
import gherkin.formatter.Filter;

import java.util.ArrayList;
import java.util.List;

public class CustomFilterConfig {
    private Class<? extends Filter> filterClass;
    private List<Object> filterParams;
    private SingleValueConverter transformer;

    public CustomFilterConfig(Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
    }

    public void addFilterParam(Object param) {
        if (filterParams == null) {
            filterParams = new ArrayList<Object>();
        }
        Object convertedParam;
        if (transformer != null && param instanceof String) {
            convertedParam = transformer.fromString((String) param);
        } else {
            convertedParam = param;
        }
        filterParams.add(convertedParam);
    }


    public List<Object> getFilterParams() {
        return filterParams;
    }

    public Class<? extends Filter> getFilterClass() {
        return filterClass;
    }

    public void setTransformer(SingleValueConverter transformer) {
        this.transformer = transformer;
    }
}
