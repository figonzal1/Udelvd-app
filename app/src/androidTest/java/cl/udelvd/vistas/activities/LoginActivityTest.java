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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
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
    public void loginActivityTest() {


        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check logo app
        ViewInteraction logoApp = onView(
                allOf(withId(R.id.iv_logo_login), withContentDescription(context.getString(R.string.LOGO_APLICACION)), isDisplayed())
        );
        logoApp.check(matches(isDisplayed()));

        //Check name app
        ViewInteraction nameApp = onView(
                allOf(withId(R.id.tv_name_app), withText(context.getString(R.string.app_name)), isDisplayed())
        );
        nameApp.check(matches(isDisplayed()));

        //Check recovery link
        ViewInteraction recovery = onView(
                allOf(withId(R.id.tv_recuperar), withText(context.getString(R.string.CUENTA_OLVIDADA)), isDisplayed())
        );
        recovery.check(matches(isDisplayed()));

        //Check register account
        ViewInteraction register = onView(
                allOf(withId(R.id.tv_registro), withText(context.getString(R.string.NO_TIENES_CUENTA)), isDisplayed())
        );
        register.check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Type email in editText
        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.et_email_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_email_login),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("tatafel_8@hotmail.com"), closeSoftKeyboard());

        //Type Password in editText
        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.et_password_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_password_login),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("123456789"), closeSoftKeyboard());

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Click on login button
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.btn_login), withText(context.getString(R.string.INICIAR_SESION)),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_login),
                                        0),
                                6)));
        materialButton.perform(scrollTo(), click());
    }
}
