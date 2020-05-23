package cl.udelvd.views.activities.events;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import cl.udelvd.R;
import cl.udelvd.views.activities.MainActivity;

import static androidx.test.espresso.Espresso.onData;
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
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NewEventTest {

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

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fb_crear_evento),
                        childAtPosition(
                                allOf(withId(R.id.event_list),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton.perform(click());
    }

    @Test
    public void test1CheckTextViews() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"), isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withText(context.getString(R.string.TITULO_TOOLBAR_CREAR_EVENTO)), isDisplayed()));
        textView.check(matches(withText(context.getString(R.string.TITULO_TOOLBAR_CREAR_EVENTO))));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.menu_save), withText(context.getString(R.string.MENU_GUARDAR_DATOS)), isDisplayed()));
        textView2.check(matches(withText(context.getString(R.string.MENU_GUARDAR_DATOS))));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tv_select_emoticon), withText(context.getString(R.string.SELECCIONAR_EMOTICON)),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                4),
                        isDisplayed()));
        textView3.check(matches(withText(context.getString(R.string.SELECCIONAR_EMOTICON))));
    }


    @Test
    public void test2NewEvent() {


        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //NEW EVENT
        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.et_event_hour),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_event_hour),
                                        0),
                                0)));
        textInputEditText6.perform(scrollTo(), click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        //SELECT ACTION
        ViewInteraction actionType = onView(withId(R.id.il_action_event));
        actionType.perform(scrollTo(), click());

        ViewInteraction actionTypeItem = onView(withText("Bath"))
                .inRoot(RootMatchers.isPlatformPopup());
        actionTypeItem.perform(click());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //JUSTIFICATION
        ViewInteraction justification = onView(
                allOf(withId(R.id.et_justification_event),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.il_justification_event),
                                        0),
                                0)));
        justification.perform(scrollTo(), replaceText("TestLastName"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //EMOTICON SPINNER
        ViewInteraction spinnerEmoticon = onView(withId(R.id.spinner_emoticon));
        spinnerEmoticon.perform(click());

        ViewInteraction spinnerEmoticonItem = onView(withText(containsString("Emoticon that describes fear")))
                .inRoot(RootMatchers.isPlatformPopup());
        spinnerEmoticonItem.perform(scrollTo(), click());


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction buttonSave = onView(
                allOf(withId(R.id.menu_save), withText(context.getString(R.string.MENU_GUARDAR_DATOS)), isDisplayed()));
        buttonSave.perform(click());
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
