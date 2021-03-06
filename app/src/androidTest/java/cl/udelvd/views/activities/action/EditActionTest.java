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
import cl.udelvd.views.activities.ActionListActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EditActionTest {

    private Context context;

    @Rule
    public ActivityTestRule<ActionListActivity> mActivityTestRule = new ActivityTestRule<>(ActionListActivity.class);

    @Before
    public void setUp() {
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
    public void editActionTest() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //CONTEXTUAL MENU
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.iv_menu_action), withContentDescription(context.getString(R.string.ICONO_MENU_CONTEXTUAL)),
                        childAtPosition(
                                allOf(withId(R.id.card_view_action),
                                        childAtPosition(
                                                withId(R.id.rv_list_action),
                                                0)),
                                6),
                        isDisplayed()));
        appCompatImageView.perform(click());

        //EDIT ACTION NAME
        ViewInteraction textView = onView(
                allOf(withId(android.R.id.title), withText(context.getString(R.string.MENU_ACCION_EDITAR)),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        textView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //CHECK SPANISH NAME IN FIRST ACTION
        ViewInteraction actionSpanishText = onView(
                allOf(withId(R.id.et_action_spanish), withText(containsString("Levantarse de la cama")),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_action_spanish),
                                        0),
                                0)));
        actionSpanishText.perform(scrollTo(), replaceText("Levantarse de la cama editado"));

        //CHECK ENGLISH NAME IN FIRST ACTION
        ViewInteraction actionEnglishText = onView(
                allOf(withId(R.id.et_action_english), withText("Wake up"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_action_english),
                                        0),
                                0)));
        actionEnglishText.perform(scrollTo(), replaceText("Wake up editado"));

        //BUTTON SAVE
        ViewInteraction buttonSave = onView(
                allOf(withId(R.id.menu_save), withText(context.getString(R.string.MENU_GUARDAR_DATOS)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        buttonSave.perform(click());
    }
}
