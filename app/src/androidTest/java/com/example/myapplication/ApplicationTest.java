package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Context context = getContext();
        Context applica = getApplication();
        Context c = getSystemContext();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}