package cl.udelvd.views.activities;


import android.content.Context;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cl.udelvd.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class SwipeViewTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testSwipeViews() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check App name in main activity
        ViewInteraction appName = onView(
                allOf(withText(context.getString(R.string.app_name)), isDisplayed()));
        appName.check(matches(withText(context.getString(R.string.app_name))));

        //Check tab name stats
        ViewInteraction statsTab = onView(
                allOf(withText(context.getString(R.string.TAB_NAME_ESTADISTICAS)), isDisplayed()));
        statsTab.check(matches(withText(context.getString(R.string.TAB_NAME_ESTADISTICAS))));
        statsTab.perform(click());

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check tab name interviewees
        ViewInteraction intervieweeTab = onView(
                allOf(withText(context.getString(R.string.TAB_NAME_ENTREVISTADOS)), isDisplayed()));
        intervieweeTab.check(matches(withText(context.getString(R.string.TAB_NAME_ENTREVISTADOS))));
        intervieweeTab.perform(click());
    }
}
