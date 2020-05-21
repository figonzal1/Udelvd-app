package cl.udelvd.views.activities.action;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//RUN TEST IN THIS ORDER
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ActionListTest.class,
        NewActionTest.class,
        EditActionTest.class
})
public class SuiteActionTestBuilder {
}
