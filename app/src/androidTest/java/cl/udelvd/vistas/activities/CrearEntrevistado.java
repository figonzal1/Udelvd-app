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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class CrearEntrevistado {

    @Rule
    public ActivityTestRule<NuevoEntrevistadoActivity> mActivityTestRule = new ActivityTestRule<>(NuevoEntrevistadoActivity.class);
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
    public void checkTextViewsTest() {
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
                allOf(withId(R.id.iv_logo_registro), isDisplayed()));
        imageView.check(matches(isDisplayed()));

        //Check save item menu
        ViewInteraction saveItem = onView(
                allOf(withId(R.id.menu_guardar), withText(context.getString(R.string.MENU_GUARDAR_DATOS)), isDisplayed()));
        saveItem.check(matches(withText(context.getString(R.string.MENU_GUARDAR_DATOS))));

        //Check Home Up button
        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"), isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        //Check Save Button
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menu_guardar), withText("Save"), isDisplayed()));
        actionMenuItemView.check(matches(isDisplayed()));
    }

    @Test
    public void newIntervieweeTest() {

        /*
        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.et_nombre_entrevistado),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_nombre_entrevistado),
                                        0),
                                0)));
        textInputEditText6.perform(scrollTo(), replaceText("TestName"), closeSoftKeyboard());

        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.et_apellido_entrevistado),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_apellido_entrevistado),
                                        0),
                                0)));
        textInputEditText7.perform(scrollTo(), replaceText("TestLastName"), closeSoftKeyboard());

        ViewInteraction checkableImageButton = onView(
                allOf(withId(R.id.text_input_end_icon), withContentDescription("Show dropdown menu"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        2),
                                0),
                        isDisplayed()));
        checkableImageButton.perform(click());

        DataInteraction textView3 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(0);
        textView3.perform(click());

        ViewInteraction textInputEditText8 = onView(
                allOf(withId(R.id.et_fecha_nacimiento),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_fecha_nacimiento),
                                        0),
                                0)));
        textInputEditText8.perform(scrollTo(), click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction checkableImageButton2 = onView(
                allOf(withId(R.id.text_input_end_icon), withContentDescription("Show dropdown menu"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        2),
                                0),
                        isDisplayed()));
        checkableImageButton2.perform(click());

        DataInteraction textView4 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        textView4.perform(click());

        ViewInteraction checkableImageButton3 = onView(
                allOf(withId(R.id.text_input_end_icon), withContentDescription("Show dropdown menu"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        2),
                                0),
                        isDisplayed()));
        checkableImageButton3.perform(click());

        DataInteraction textView5 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(0);
        textView5.perform(click());

        ViewInteraction textInputEditText9 = onView(
                allOf(withId(R.id.et_n_convivientes_entrevistado),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_n_convivientes_entrevistado),
                                        0),
                                0)));
        textInputEditText9.perform(scrollTo(), replaceText("1"), closeSoftKeyboard());

        ViewInteraction switchMaterial = onView(
                allOf(withId(R.id.switch_jubilado_legal), withText("Legal retiree"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                10)));
        switchMaterial.perform(scrollTo(), click());

        ViewInteraction checkableImageButton4 = onView(
                allOf(withId(R.id.text_input_end_icon), withContentDescription("Show dropdown menu"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        2),
                                0),
                        isDisplayed()));
        checkableImageButton4.perform(click());

        DataInteraction textView6 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        textView6.perform(click());

        ViewInteraction checkableImageButton5 = onView(
                allOf(withId(R.id.text_input_end_icon), withContentDescription("Show dropdown menu"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        2),
                                0),
                        isDisplayed()));
        checkableImageButton5.perform(click());

        DataInteraction textView7 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        textView7.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menu_guardar), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        */

    }
}
