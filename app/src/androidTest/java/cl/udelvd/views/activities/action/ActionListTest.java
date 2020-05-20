package cl.udelvd.views.activities.action;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cl.udelvd.R;
import cl.udelvd.views.activities.ActionListActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class ActionListTest {

    @Rule
    public ActivityTestRule<ActionListActivity> mActivityTestRule = new ActivityTestRule<>(ActionListActivity.class);

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void checkViewsTest() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Home button
        ViewInteraction homButton = onView(
                allOf(withContentDescription("Navigate up"), isDisplayed()));
        homButton.check(matches(isDisplayed()));

        //Toolbar name
        ViewInteraction toolbarName = onView(
                allOf(withText("Actions List"), isDisplayed()));
        toolbarName.check(matches(withText("Actions List")));

        //Refresh icon
        ViewInteraction refreshIcon = onView(
                allOf(withId(R.id.menu_refresh), withContentDescription("Update"), isDisplayed()));
        refreshIcon.check(matches(isDisplayed()));

        //Fab icon (new action)
        ViewInteraction fabIcon = onView(
                allOf(withId(R.id.fb_new_action),
                        childAtPosition(
                                allOf(withId(R.id.action_list),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        fabIcon.check(matches(isDisplayed()));

        //Check name of first action in list
        ViewInteraction actionItemName = onView(
                allOf(withId(R.id.tv_action), withText("Action 1"), isDisplayed()));
        actionItemName.check(matches(isDisplayed()));

        //Spanish title action
        ViewInteraction actionItemSpanish = onView(
                allOf(withId(R.id.tv_spanish), withText("Levantarse de la cama"), isDisplayed()));
        actionItemSpanish.check(matches(isDisplayed()));

        //English title action
        ViewInteraction actionItemEnglish = onView(
                allOf(withId(R.id.tv_english), withText("Wake up"), isDisplayed()));
        actionItemEnglish.check(matches(isDisplayed()));
    }

    @Test
    public void createActionTest() {

    }
}
