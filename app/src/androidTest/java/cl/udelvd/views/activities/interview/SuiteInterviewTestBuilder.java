package cl.udelvd.views.activities.interview;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//RUN TEST IN THIS ORDER
@RunWith(Suite.class)
@Suite.SuiteClasses({
        NewInterviewTest.class,
        ContextMenuInterviews.class,
        EditInterviewTest.class,
        DeleteInterviewTest.class
})
public class SuiteInterviewTestBuilder {
}
