package cucumber.runtime;

import gherkin.formatter.*;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomFilterFormatterTest {
    @Test
    public void is_backward_compatible() throws Exception{
        assertThat(getFilter(new CustomFilterFormatter(null, Arrays.asList("@tag1"))),is(instanceOf(TagFilter.class)));
        assertThat(getFilter(new CustomFilterFormatter(null, Arrays.asList(Pattern.compile("Feature1")))),is(instanceOf(PatternFilter.class)));
        assertThat(getFilter(new CustomFilterFormatter(null, Arrays.asList(1))),is(instanceOf(LineFilter.class)));
        try{
            new CustomFilterFormatter(null, Arrays.asList(new Date()));
            fail("RuntimeException expected");
        }catch(RuntimeException e){
            assertTrue(e.getMessage().startsWith("Could not create filter method for unknown filter"));
        }
        try{
            new CustomFilterFormatter(null, Arrays.asList(new Integer(1),"@tag1"));
            fail("IllegalArgumentException expected");
        }catch(IllegalArgumentException  e){
           assertTrue(e.getMessage().startsWith("Inconsistent filter"));
        }
    }
    @Test
    public void creates_customizeable_filters_for_filter_config_objects() throws Exception{
        CustomFilterConfig filterConfig = new CustomFilterConfig(LineFilter.class);
        filterConfig.addFilterParam(4);
        assertThat(getFilter(new CustomFilterFormatter(null, Arrays.asList(filterConfig))),is(instanceOf(CustomizableFilter.class)));
    }
    Filter getFilter(CustomFilterFormatter formatter) throws Exception{
        Field filter = FilterFormatter.class.getDeclaredField("filter");
        filter.setAccessible(true);
        return (Filter) filter.get(formatter);
    }
}
