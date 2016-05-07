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
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class CreateAccountTest {
    @Rule public final ActivityTestRule<CreateAccount> main = new ActivityTestRule<>(CreateAccount.class);

    @Test
    public void testIncompleteInfo() {
        onView(withId(R.id.create)).perform(ViewActions.click());
        onView(withText(R.string.incomplete_info_toast)).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void testCreate(){

        // Make sure your database is empty
        // Make sure server is running
        onView(withId(R.id.firstName)).perform(ViewActions.replaceText("John Doe"));
        onView(withId(R.id.email)).perform(ViewActions.replaceText("johndoe@aol.com"));
        onView(withId(R.id.username)).perform(ViewActions.replaceText("johnthedon"));
        onView(withId(R.id.password)).perform(ViewActions.replaceText("secretword"));
        onView(withId(R.id.confirmPassword)).perform(ViewActions.replaceText("secretword"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.create)).perform(ViewActions.click());
        onView(withText(R.string.success_create_account_toast)).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

}
