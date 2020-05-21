package cl.udelvd.views.activities.interviewee;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//RUN TEST IN THIS ORDER
@RunWith(Suite.class)
@Suite.SuiteClasses({
        NewIntervieweeTest.class,
        ContextMenuInterviewee.class,
        EditIntervieweeTest.class,
        DeleteIntervieweeTest.class
})
public class SuiteIntervieweeTestBuilder {
}
