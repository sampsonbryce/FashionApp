package com.example.samps_000.fashionapp;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    @Rule
    public final ActivityTestRule<Login> main = new ActivityTestRule<>(Login.class);

    @Test
    public void testIncompleteLoginInfo() {
        onView(withId(R.id.loginButton)).perform(ViewActions.click());
        onView(withText(R.string.incomplete_info_toast)).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void testLogin(){
        onView(withId(R.id.loginEmail)).perform(ViewActions.replaceText("definietly not and account"));
        onView(withId(R.id.loginPassword)).perform(ViewActions.replaceText("shouldnt be a password"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(ViewActions.click());
        onView(withText(R.string.failed_login_toast)).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }
}
