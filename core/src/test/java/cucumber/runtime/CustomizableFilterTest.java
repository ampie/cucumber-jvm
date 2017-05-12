package cucumber.runtime;

import gherkin.formatter.Filter;
import gherkin.formatter.LineFilter;
import gherkin.formatter.TagFilter;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.ExamplesTableRow;
import gherkin.formatter.model.Range;
import gherkin.formatter.model.Tag;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CustomizableFilterTest {
    @Test
    public void should_apply_all_filters(){
        CustomFilterConfig tagFilter1 = new CustomFilterConfig(TagFilter.class);
        tagFilter1.addFilterParam("@tag1");
        CustomFilterConfig tagFilter2 = new CustomFilterConfig(TagFilter.class);
        tagFilter2.addFilterParam("@tag2");
        CustomizableFilter customizableFilter = new CustomizableFilter(Arrays.asList(tagFilter1, tagFilter2));
        List<Tag> tags = Arrays.asList(new Tag("@tag1", 5), new Tag("@tag2", 6));
        assertTrue(customizableFilter.evaluate(tags, Collections.<String>emptyList(),Collections.<Range>emptyList()));
    }
    @Test
    public void should_fail_when_any_filter_fails(){
        CustomFilterConfig tagFilter1 = new CustomFilterConfig(TagFilter.class);
        tagFilter1.addFilterParam("@tag1");
        CustomFilterConfig tagFilter2 = new CustomFilterConfig(TagFilter.class);
        tagFilter2.addFilterParam("@tag2");
        CustomizableFilter customizableFilter = new CustomizableFilter(Arrays.asList(tagFilter1, tagFilter2));
        List<Tag> tags = Arrays.asList(new Tag("@tag1", 5), new Tag("@tag3", 6));
        assertFalse(customizableFilter.evaluate(tags, Collections.<String>emptyList(),Collections.<Range>emptyList()));
    }
    @Test
    public void should_only_select_table_rows_that_all_filters_allow(){
        CustomFilterConfig tagFilter1 = new CustomFilterConfig(TagFilter.class);
        tagFilter1.addFilterParam("@tag1");
        CustomFilterConfig tagFilter2 = new CustomFilterConfig(LineFilter.class);
        tagFilter2.addFilterParam(5);
        CustomizableFilter customizableFilter = new CustomizableFilter(Arrays.asList(tagFilter1, tagFilter2));

        List<ExamplesTableRow> rows=new ArrayList<ExamplesTableRow>();
        rows.add(new ExamplesTableRow(Collections.<Comment>emptyList(),Collections.<String>emptyList(),4,"header"));
        rows.add(new ExamplesTableRow(Collections.<Comment>emptyList(),Collections.<String>emptyList(),5,"row1"));
        rows.add(new ExamplesTableRow(Collections.<Comment>emptyList(),Collections.<String>emptyList(),6,"row2"));
        List<ExamplesTableRow> filteredRows = customizableFilter.filterTableBodyRows(rows);
        assertEquals(2, filteredRows.size());
        assertEquals("header", filteredRows.get(0).getId());
        assertEquals("row1", filteredRows.get(1).getId());
    }
    @Test
    public void only_allows_constructors_with_no_parameters_or_a_list_parameter(){
        CustomFilterConfig listConstructorFilter = new CustomFilterConfig(TagFilter.class);
        listConstructorFilter.addFilterParam("@tag1");
        CustomFilterConfig emptyConstructorFilter = new CustomFilterConfig(ValidFilter.class);
        CustomFilterConfig invalidFilter = new CustomFilterConfig(InvalidFilter.class);
        new CustomizableFilter(Arrays.asList(listConstructorFilter, emptyConstructorFilter));
        try{
            new CustomizableFilter(Arrays.asList(listConstructorFilter, invalidFilter));
            fail("Should not have allowed the invalid filter to be instantiated");
        }catch(CucumberException ce){
            assertTrue(ce.getMessage().startsWith("Could not find a suitable constructor"));
        }
    }
    @Test
    public void wraps_target_exceptions_in_cucumber_Exceptions(){
        CustomFilterConfig listConstructorFilter = new CustomFilterConfig(TagFilter.class);
        try{
            new CustomizableFilter(Arrays.asList(listConstructorFilter));
            fail("Should not have allowed the invalid filter to be instantiated");
        }catch(CucumberException ce){
            assertTrue(ce.getMessage().startsWith("Could not instantiate filter"));
        }
    }
    public static class InvalidFilter implements Filter{
        public InvalidFilter(String param){

        }

        @Override
        public boolean evaluate(List<Tag> tags, List<String> names, List<Range> ranges) {
            return false;
        }

        @Override
        public List<ExamplesTableRow> filterTableBodyRows(List<ExamplesTableRow> examplesRows) {
            return null;
        }
    }
    public static class ValidFilter implements Filter{

        @Override
        public boolean evaluate(List<Tag> tags, List<String> names, List<Range> ranges) {
            return false;
        }

        @Override
        public List<ExamplesTableRow> filterTableBodyRows(List<ExamplesTableRow> examplesRows) {
            return null;
        }
    }

}
