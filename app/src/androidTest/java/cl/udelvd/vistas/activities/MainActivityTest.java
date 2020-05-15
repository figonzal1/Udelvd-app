package cl.udelvd.vistas.activities;


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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

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
    }

    @Test
    public void checkDisplayingTextViewsTest() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_entrevistados_vacios), withText("You don't have interviewees yet"), isDisplayed()));
        textView.check(matches(withText("You don't have interviewees yet")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_n_entrevistados), withText("Displaying 0 of 0 interviewees"), isDisplayed()));
        textView2.check(matches(withText("Displaying 0 of 0 interviewees")));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fb_crear_usuario),
                        childAtPosition(
                                allOf(withId(R.id.entrevistados_lista),
                                        withParent(withId(R.id.view_pager_main))),
                                1),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));
    }

    @Test
    public void clickFloatingButtonNewIntervieweeTest() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fb_crear_usuario),
                        childAtPosition(
                                allOf(withId(R.id.entrevistados_lista),
                                        withParent(withId(R.id.view_pager_main))),
                                1),
                        isDisplayed()));
        floatingActionButton.perform(click());
    }
}
