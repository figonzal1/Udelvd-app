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
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class DrawerActivityTest {

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

        //Press home button to open drawer layout
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.include),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());
    }

    @Test
    public void checkTitlesAndUserInformation() {


        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check the rol of researcher
        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_header_nombre_rol), isDisplayed())
        );
        textView.check(matches(withText(context.getString(R.string.ROL_INVESTIGADOR))));

        //Check the name of researcher in the drawer header
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_header_nombre_apellido_usuario), withText("TestName TestLastName"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_header_container),
                                        0),
                                2),
                        isDisplayed()));
        textView2.check(matches(withText("TestName TestLastName")));

        //Check the email of researcher in the drawer header
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tv_header_email_usuario), withText("tatafel_8@hotmail.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_header_container),
                                        0),
                                3),
                        isDisplayed()));
        textView3.check(matches(withText("tatafel_8@hotmail.com")));

        //Match the item My profile
        ViewInteraction checkedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText(context.getString(R.string.MENU_MI_PERFIL)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.design_navigation_view),
                                        2),
                                0),
                        isDisplayed()));
        checkedTextView.check(matches(withText(context.getString(R.string.MENU_MI_PERFIL))));

        //Match the item Interviewees
        ViewInteraction checkedTextView2 = onView(
                allOf(withId(R.id.design_menu_item_text), withText(context.getString(R.string.MENU_LISTA_ENTREVISTADOS)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.design_navigation_view),
                                        3),
                                0),
                        isDisplayed()));
        checkedTextView2.check(matches(withText(context.getString(R.string.MENU_LISTA_ENTREVISTADOS))));

        //Match the item Stats
        ViewInteraction checkedTextView3 = onView(
                allOf(withId(R.id.design_menu_item_text), withText(context.getString(R.string.MENU_ESTADISTICAS)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.design_navigation_view),
                                        4),
                                0),
                        isDisplayed()));
        checkedTextView3.check(matches(withText(context.getString(R.string.MENU_ESTADISTICAS))));
    }

    @Test
    public void pressMyProfileItemMenu() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Press the button item My profile
        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navigation_view),
                                        0)),
                        2),
                        isDisplayed()));
        navigationMenuItemView.perform(click());
    }

    @Test
    public void pressIntervieweesItemMenu() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Press the button item Interviewees list
        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navigation_view),
                                        0)),
                        3),
                        isDisplayed()));
        navigationMenuItemView.perform(click());
    }

    @Test
    public void pressStatisticsItemMenu() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Press the button item Stats
        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navigation_view),
                                        0)),
                        4),
                        isDisplayed()));
        navigationMenuItemView.perform(click());
    }
}
