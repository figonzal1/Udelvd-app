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
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import cl.udelvd.R;
import cl.udelvd.views.activities.NewIntervieweeActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NewIntervieweeTest {

    @Rule
    public ActivityTestRule<NewIntervieweeActivity> mActivityTestRule = new ActivityTestRule<>(NewIntervieweeActivity.class);
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
    public void test1CheckTextViews() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Check title of activity
        ViewInteraction toolbarTitle = onView(
                allOf(withText(context.getString(R.string.TITULO_TOOLBAR_NUEVO_ENTREVISTADO)), isDisplayed()));
        toolbarTitle.check(matches(withText(context.getString(R.string.TITULO_TOOLBAR_NUEVO_ENTREVISTADO))));

        //Check register icon
        ViewInteraction imageView = onView(
                allOf(withId(R.id.iv_logo_registry), isDisplayed()));
        imageView.check(matches(isDisplayed()));

        //Check save item menu
        ViewInteraction saveItem = onView(
                allOf(withId(R.id.menu_save), withText(context.getString(R.string.MENU_GUARDAR_DATOS)), isDisplayed()));
        saveItem.check(matches(withText(context.getString(R.string.MENU_GUARDAR_DATOS))));

        //Check Home Up button
        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"), isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        //Check Save Button
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menu_save), withText(context.getString(R.string.MENU_GUARDAR_DATOS)), isDisplayed()));
        actionMenuItemView.check(matches(isDisplayed()));
    }

    @Test
    public void test2NewInterviewee() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction etName = onView(
                allOf(withId(R.id.et_interview_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_interview_name),
                                        0),
                                0)));
        etName.perform(scrollTo(), replaceText("TestName"));

        ViewInteraction etLastName = onView(
                allOf(withId(R.id.et_interview_last_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_interview_last_name),
                                        0),
                                0)));
        etLastName.perform(scrollTo(), replaceText("TestLastName"));

        //GENRE
        ViewInteraction genreSpinner = onView(withId(R.id.il_interviewee_genre));
        genreSpinner.perform(click());

        ViewInteraction genreSpinnerItem = onView(withText("Male"))
                .inRoot(RootMatchers.isPlatformPopup());
        genreSpinnerItem.perform(click());


        ViewInteraction openDateDialog = onView(
                allOf(withId(R.id.et_birth_date),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_birth_date),
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

        //CITY SPINNER
        ViewInteraction citySpinner = onView(withId(R.id.il_interview_city));
        citySpinner.perform(click());

        ViewInteraction citySpinnerItem = onView(withText("Santiago"))
                .inRoot(RootMatchers.isPlatformPopup());
        citySpinnerItem.perform(click());

        //INTERVIEWEE SPINNER
        ViewInteraction civilStatusSpinner = onView(withId(R.id.il_interviewee_civil_state));
        civilStatusSpinner.perform(click());

        ViewInteraction civilStatusSpinnerItem = onView(withText("Single"))
                .inRoot(RootMatchers.isPlatformPopup());
        civilStatusSpinnerItem.perform(click());

        //COEXISTENCe
        ViewInteraction textInputEditText9 = onView(
                allOf(withId(R.id.et_n_coexistence_interviewee),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_n_coexistence_interviewee),
                                        0),
                                0)));
        textInputEditText9.perform(scrollTo(), replaceText("1"));

        ViewInteraction switchMaterial = onView(
                allOf(withId(R.id.switch_retire_legal), withText("Legal retiree"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                10)));
        switchMaterial.perform(scrollTo(), click(), closeSoftKeyboard());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //EDUCATIONAL SPINNER
        ViewInteraction educationalStatusSpinner = onView(withId(R.id.il_interviewee_educational_level));
        educationalStatusSpinner.perform(scrollTo(), click());

        ViewInteraction educationalStatusSpinnerItem = onView(withText("Primary education incomplete"))
                .inRoot(RootMatchers.isPlatformPopup());
        educationalStatusSpinnerItem.perform(click());

        //COEXISTENCe TYPE SPINNER
        ViewInteraction coexistenceStatusSpinner = onView(withId(R.id.il_interviewee_coexistence_type));
        coexistenceStatusSpinner.perform(scrollTo(), click());

        ViewInteraction coexistencestatusSpinnerItem = onView(withText("Alone"))
                .inRoot(RootMatchers.isPlatformPopup());
        coexistencestatusSpinnerItem.perform(click());

        //PROFESSION SPINNER
        ViewInteraction professionStatusSpinner = onView(withId(R.id.il_interviewee_profession));
        professionStatusSpinner.perform(click());

        ViewInteraction professionStatusSpinnerItem = onView(withText("Ingeniero"))
                .inRoot(RootMatchers.isPlatformPopup());
        professionStatusSpinnerItem.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
