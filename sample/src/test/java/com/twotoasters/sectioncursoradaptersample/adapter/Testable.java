package com.twotoasters.sectioncursoradaptersample.adapter;

import android.os.Build.VERSION_CODES;

import com.twotoasters.sectioncursoradaptersample.BuildConfig;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * All tests require these annotation params.
 * As far as I can tell you cannot set these params globally
 * with Robolectric 3.0 so you must extend this class for all tests.
 *
 * Current highest sdk level support is 21.
 *
 * @author Chris Pierick
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        packageName = "com.ticketmaster.mobilestudio.soundcheck",
        sdk = VERSION_CODES.LOLLIPOP) // Max android sdk level that Robolectric supports.
public abstract class Testable { }
