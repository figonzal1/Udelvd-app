package cl.udelvd.views.activities.interviewee;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.RootMatchers;
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
import cl.udelvd.views.activities.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class EditIntervieweeTest {

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
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void editIntervieweeTest() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //CONTEXTUAL MENU
        ViewInteraction contextualMenuIcon = onView(
                allOf(withId(R.id.iv_menu_interviewee), withContentDescription(context.getString(R.string.ICONO_MENU_CONTEXTUAL)),
                        childAtPosition(
                                allOf(withId(R.id.card_view_interviewee),
                                        childAtPosition(
                                                withId(R.id.rv_interviewee_list),
                                                0)),
                                8),
                        isDisplayed()));
        contextualMenuIcon.perform(click());

        //EDIT INTERVIEWEE MENU ITEM
        ViewInteraction editMenuItem = onView(
                allOf(withId(android.R.id.title), withText(context.getString(R.string.MENU_ENTREVISTADO_EDITAR)),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        editMenuItem.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check title of activity
        ViewInteraction toolbarTitle = onView(
                allOf(withText(context.getString(R.string.TITULO_TOOLBAR_EDITAR_ENTREVISTADO)), isDisplayed()));
        toolbarTitle.check(matches(withText(context.getString(R.string.TITULO_TOOLBAR_EDITAR_ENTREVISTADO))));

        //INTERVIEWEE NAME
        ViewInteraction etName = onView(
                allOf(withId(R.id.et_interview_name), withText("TestName"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_interview_name),
                                        0),
                                0)));
        etName.perform(scrollTo(), replaceText("TestNameEditado"));

        //INTERVIEWEE LAST NAME
        ViewInteraction etLastName = onView(
                allOf(withId(R.id.et_interview_last_name), withText("TestLastName"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_interview_last_name),
                                        0),
                                0)));
        etLastName.perform(scrollTo(), replaceText("TestLastNameEditado"));

        //GENRE
        ViewInteraction genreSpinner = onView(withId(R.id.il_interviewee_genre));
        genreSpinner.perform(click());

        ViewInteraction genreSpinnerItem = onView(withText(context.getString(R.string.SEXO_OTRO)))
                .inRoot(RootMatchers.isPlatformPopup());
        genreSpinnerItem.perform(click());

        //INTERVIEWEE CIVIL STATE
        ViewInteraction civilStatusSpinner = onView(withId(R.id.il_interviewee_civil_state));
        civilStatusSpinner.perform(click());

        ViewInteraction civilStatusSpinnerItem = onView(withText("Divorce"))
                .inRoot(RootMatchers.isPlatformPopup());
        civilStatusSpinnerItem.perform(click());

        //CLICK SAVE
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menu_save), withText(context.getString(R.string.MENU_GUARDAR_DATOS)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());
    }
}
