package cl.udelvd.views.activities.events;


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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteEventTest {

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

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.iv_menu_interviewee), withContentDescription(context.getString(R.string.ICONO_MENU_CONTEXTUAL)),
                        childAtPosition(
                                allOf(withId(R.id.card_view_interviewee),
                                        childAtPosition(
                                                withId(R.id.rv_interviewee_list),
                                                0)),
                                8),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction materialTextView = onView(
                allOf(withId(android.R.id.title), withText(context.getString(R.string.MENU_ENTREVISTADO_VER_ENTREVISTAS)),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView.perform(click());

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.iv_menu_interview), withContentDescription(context.getString(R.string.ICONO_MENU_CONTEXTUAL)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card_view_interview),
                                        0),
                                5),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(android.R.id.title), withText(context.getString(R.string.MENU_ENTREVISTA_VER_EVENTO)),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());
    }

    @Test
    public void test1ClickCancelDeleteEvent() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fb_delete_event),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.card.MaterialCardView")),
                                        0),
                                19)));
        floatingActionButton.perform(scrollTo(), click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button2), withText(context.getString(R.string.DIALOG_NEGATIVE_BTN)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                2)));
        materialButton2.perform(scrollTo(), click());
    }

    @Test
    public void test2ClickDeleteEvent() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.fb_delete_event),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.card.MaterialCardView")),
                                        0),
                                19)));
        floatingActionButton2.perform(scrollTo(), click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button1), withText(context.getString(R.string.DIALOG_POSITIVE_BTN)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton3.perform(scrollTo(), click());
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
