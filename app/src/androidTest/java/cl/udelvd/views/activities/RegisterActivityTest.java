package cl.udelvd.views.activities;


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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cl.udelvd.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegistryActivity> mActivityTestRule = new ActivityTestRule<>(RegistryActivity.class);
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
    }

    @Test
    public void registerActivityTest() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check title of activity
        ViewInteraction toolbarTitle = onView(
                allOf(withText(context.getString(R.string.TITULO_TOOLBAR_REGISTRO)), isDisplayed()));
        toolbarTitle.check(matches(withText(context.getString(R.string.TITULO_TOOLBAR_REGISTRO))));

        ///Scroll to the register button and click it
        ViewInteraction registerButton = onView(
                allOf(withId(R.id.btn_registry), withText(context.getString(R.string.REGISTRAR)),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                11)));
        registerButton.perform(scrollTo(), click());

        //Type the name of researcher in edit text
        ViewInteraction name = onView(
                allOf(withId(R.id.et_researcher_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_researcher_name),
                                        0),
                                0)));
        name.perform(scrollTo(), replaceText("TestName"), closeSoftKeyboard());

        //Type last name of researcher edit text
        ViewInteraction lastName = onView(
                allOf(withId(R.id.et_researcher_last_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_researcher_last_name),
                                        0),
                                0)));
        lastName.perform(scrollTo(), replaceText("TestLastName"), closeSoftKeyboard());

        //Type the email of researcher
        ViewInteraction researchEmail = onView(
                allOf(withId(R.id.et_researcher_email),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_researcher_email),
                                        0),
                                0)));
        researchEmail.perform(scrollTo(), replaceText("tatafel_8@hotmail.com"), closeSoftKeyboard());

        //Type the password
        ViewInteraction password = onView(
                allOf(withId(R.id.et_researcher_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_researcher_password),
                                        0),
                                0)));
        password.perform(scrollTo(), replaceText("123456789"), closeSoftKeyboard());

        //Confirm password
        ViewInteraction confirmPassword = onView(
                allOf(withId(R.id.et_research_confirm_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_research_confirm_password),
                                        0),
                                0)));
        confirmPassword.perform(scrollTo(), replaceText("123456789"), closeSoftKeyboard());

        //Click on register button again
        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.btn_registry), withText(context.getString(R.string.REGISTRAR)),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                11)));
        materialButton2.perform(scrollTo(), click());

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
