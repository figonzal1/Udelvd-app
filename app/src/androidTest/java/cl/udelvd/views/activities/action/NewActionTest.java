package cl.udelvd.views.activities.action;

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
import cl.udelvd.views.activities.NewActionActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NewActionTest {

    @Rule
    public ActivityTestRule<NewActionActivity> mActivityTestRule = new ActivityTestRule<>(NewActionActivity.class);
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

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
    public void test1CheckForm() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check title of activity
        ViewInteraction toolbarTitle = onView(
                allOf(withText(context.getString(R.string.TITULO_TOOLBAR_NUEVA_ACCION)), isDisplayed()));
        toolbarTitle.check(matches(withText(context.getString(R.string.TITULO_TOOLBAR_NUEVA_ACCION))));

        //Check save item menu
        ViewInteraction saveItem = onView(
                allOf(withId(R.id.menu_save), withText(context.getString(R.string.MENU_GUARDAR_DATOS)), isDisplayed()));
        saveItem.check(matches(withText(context.getString(R.string.MENU_GUARDAR_DATOS))));

        //Check Home Up button
        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"), isDisplayed()));
        imageButton.check(matches(isDisplayed()));
    }

    @Test
    public void test2NewAction() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction actionSpanish = onView(withId(R.id.et_action_spanish));
        actionSpanish.perform(scrollTo(), replaceText("TestAccion"));

        ViewInteraction actionEnglish = onView(
                withId(R.id.et_action_english));
        actionEnglish.perform(scrollTo(), replaceText("TestAction"));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menu_save), withText(context.getString(R.string.MENU_GUARDAR_DATOS)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
/*
        //CHECK SPANISH NAME IN FIRST ACTION
        ViewInteraction actionSpanishText = onView(
                allOf(withId(R.id.et_action_spanish), withText(containsString("TestAccion")),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_action_spanish),
                                        0),
                                0)));
        actionSpanishText.perform(scrollTo(), replaceText("TestAccion"));

        //CHECK ENGLISH NAME IN FIRST ACTION
        ViewInteraction actionEnglishText = onView(
                allOf(withId(R.id.et_action_english), withText("TestAction"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_action_english),
                                        0),
                                0)));
        actionEnglishText.perform(scrollTo(), replaceText("TestAction"));
        */

    }
}
