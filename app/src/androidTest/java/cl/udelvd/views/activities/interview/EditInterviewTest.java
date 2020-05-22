package cl.udelvd.views.activities.interview;

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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class EditInterviewTest {

    private Context context;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //OPEN CONTEXTUAL MENU INTERVIEWEE
        ViewInteraction contextMenuInterviewee = onView(
                allOf(withId(R.id.iv_menu_interviewee), withContentDescription(context.getString(R.string.ICONO_MENU_CONTEXTUAL)),
                        childAtPosition(
                                allOf(withId(R.id.card_view_interviewee),
                                        childAtPosition(
                                                withId(R.id.rv_interviewee_list),
                                                0)),
                                8),
                        isDisplayed()));
        contextMenuInterviewee.perform(click());

        //CLICK ON SEE INTERVIEWS
        ViewInteraction showInterview = onView(
                allOf(withId(android.R.id.title), withText(context.getString(R.string.MENU_ENTREVISTADO_VER_ENTREVISTAS)),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        showInterview.perform(click());
    }

    @Test
    public void test1EditInterview() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //OPEN CONTEXTUAL MENU INTERVIEW
        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.iv_menu_interview), withContentDescription(context.getString(R.string.ICONO_MENU_CONTEXTUAL)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card_view_interview),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        //CLICK EDIT INTERVIEW
        ViewInteraction materialTextView2 = onView(
                allOf(withId(android.R.id.title), withText(context.getString(R.string.MENU_ENTREVISTA_EDITAR)),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //EDIT INVTERIVEW
        //INTERVIEW TYPE
        ViewInteraction interviewType = onView(withId(R.id.il_interview_type));
        interviewType.perform(scrollTo(), click());

        ViewInteraction interviewTypeItem = onView(withText("Extraordinary"))
                .inRoot(RootMatchers.isPlatformPopup());
        interviewTypeItem.perform(click());

        //OPEN DATE DIALOG
        ViewInteraction openDateDialog = onView(
                allOf(withId(R.id.et_interview_date),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_interview_date),
                                        0),
                                0)));
        openDateDialog.perform(scrollTo(), click());

        ViewInteraction clickOk = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        clickOk.perform(scrollTo(), click());

        //BUTTON SAVE
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
}
