package cl.udelvd.views.activities.interview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import cl.udelvd.R;
import cl.udelvd.views.activities.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContextMenuInterviews {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private Context context;

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

    @Before
    public void setup() {

        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //CLick in firt interviewee of main list
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.rv_interviewee_list),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                5))
        );
        recyclerView.perform(actionOnItemAtPosition(0, click()));
    }

    @Test
    public void test1CheckViews() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withText("Interviews List"), isDisplayed()));
        textView.check(matches(withText("Interviews List")));

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"), isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.menu_refresh), withContentDescription("Update"), isDisplayed()));
        textView2.check(matches(withText("")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tv_interviewee_name), withText("TestName TestLastName"), isDisplayed()));
        textView3.check(matches(withText("TestName TestLastName")));

        //Check fab button
        ViewInteraction imageButton2 = onView(
                allOf(withId(R.id.fb_new_interview),
                        childAtPosition(
                                allOf(withId(R.id.interviewees_list),
                                        childAtPosition(
                                                withId(R.id.interviews_list),
                                                1)),
                                1),
                        isDisplayed()));
        imageButton2.check(matches(isDisplayed()));
    }

    @Test
    public void test2ContextMenu() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.iv_interview_menu), withContentDescription("Contextual menu icon"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card_view_interview),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction showEventItem = onView(
                allOf(withId(android.R.id.title), withText("Show events"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        showEventItem.check(matches(isDisplayed()));

        ViewInteraction editInterviewItem = onView(
                allOf(withId(android.R.id.title), withText("Edit interview"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        editInterviewItem.check(matches(isDisplayed()));

        ViewInteraction deleteInterviewItem = onView(
                allOf(withId(android.R.id.title), withText("Delete interview"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        deleteInterviewItem.check(matches(isDisplayed()));
    }

    @Test
    public void test3OPenShowEvents() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.iv_interview_menu), withContentDescription("Contextual menu icon"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card_view_interview),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction showEventItem = onView(
                allOf(withId(android.R.id.title), withText("Show events"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        showEventItem.perform(click());
    }

    @Test
    public void test4OpenEditInterview() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.iv_interview_menu), withContentDescription("Contextual menu icon"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card_view_interview),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction editInterviewItem = onView(
                allOf(withId(android.R.id.title), withText("Edit interview"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        editInterviewItem.perform(click());
    }

    @Test
    public void test5OpenDeleteInterview() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.iv_interview_menu), withContentDescription("Contextual menu icon"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card_view_interview),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction deleteInterviewItem = onView(
                allOf(withId(android.R.id.title), withText("Delete interview"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        deleteInterviewItem.perform(click());
    }
}
