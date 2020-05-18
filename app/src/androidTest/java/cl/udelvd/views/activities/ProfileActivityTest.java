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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class ProfileActivityTest {

    @Rule
    public ActivityTestRule<ProfileActivity> mActivityTestRule = new ActivityTestRule<>(ProfileActivity.class);
    private Context mContext;

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
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void profileActivityTest() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check title of activity
        ViewInteraction toolbarTitle = onView(
                allOf(withText(mContext.getString(R.string.TITULO_TOOLBAR_PERFIL)), isDisplayed()));
        toolbarTitle.check(matches(withText(mContext.getString(R.string.TITULO_TOOLBAR_PERFIL))));

        //Check research name
        ViewInteraction researchName = onView(
                allOf(withId(R.id.tv_researcher_name), withText("TestName TestLastName"), isDisplayed())
        );
        researchName.check(matches(withText("TestName TestLastName")));

        //Check match research email
        ViewInteraction researchMail = onView(
                allOf(withId(R.id.tv_email_researcher), withText("tatafel_8@hotmail.com"), isDisplayed())
        );
        researchMail.check(matches(withText("tatafel_8@hotmail.com")));

        //Check match status account
        ViewInteraction researchStatus = onView(
                allOf(withId(R.id.tv_activated_researcher), withText(mContext.getString(R.string.PERFIL_ACTIVADO)), isDisplayed())
        );
        researchStatus.check(matches(withText(mContext.getString(R.string.PERFIL_ACTIVADO))));

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Open overflow menu
        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());


        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Click on edit profile menu item on overflow menu
        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.title), withText(mContext.getString(R.string.MENU_PERFIL_EDITAR)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView.perform(click());
    }
}
