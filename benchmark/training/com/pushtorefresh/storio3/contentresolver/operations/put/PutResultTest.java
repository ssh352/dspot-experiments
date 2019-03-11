package com.pushtorefresh.storio3.contentresolver.operations.put;


import android.net.Uri;
import com.pushtorefresh.storio3.contentresolver.BuildConfig;
import com.pushtorefresh.storio3.test.ToStringChecker;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// Required for correct Uri impl
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PutResultTest {
    @Test
    public void createInsertResult() {
        final Uri insertedUri = Mockito.mock(Uri.class);
        final Uri affectedUri = Mockito.mock(Uri.class);
        final PutResult insertResult = PutResult.newInsertResult(insertedUri, affectedUri);
        assertThat(insertResult.wasInserted()).isTrue();
        assertThat(insertResult.wasUpdated()).isFalse();
        assertThat(insertResult.wasNotInserted()).isFalse();
        assertThat(insertResult.wasNotUpdated()).isTrue();
        assertThat(insertResult.insertedUri()).isSameAs(insertedUri);
        assertThat(insertResult.affectedUri()).isSameAs(affectedUri);
        assertThat(insertResult.numberOfRowsUpdated()).isNull();
    }

    @Test
    public void shouldNotCreateInsertResultWithNullInsertedUri() {
        try {
            // noinspection ConstantConditions
            PutResult.newInsertResult(null, Mockito.mock(Uri.class));
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("insertedUri must not be null");
        }
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedUri() {
        try {
            // noinspection ConstantConditions
            PutResult.newInsertResult(Mockito.mock(Uri.class), null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("affectedUri must not be null");
        }
    }

    @Test
    public void createUpdateResult() {
        final int numberOfRowsUpdated = 10;
        final Uri affectedUri = Mockito.mock(Uri.class);
        final PutResult updateResult = PutResult.newUpdateResult(numberOfRowsUpdated, affectedUri);
        assertThat(updateResult.wasUpdated()).isTrue();
        assertThat(updateResult.wasInserted()).isFalse();
        assertThat(updateResult.wasNotUpdated()).isFalse();
        assertThat(updateResult.wasNotInserted()).isTrue();
        // noinspection ConstantConditions
        assertThat(((int) (updateResult.numberOfRowsUpdated()))).isEqualTo(numberOfRowsUpdated);
        assertThat(updateResult.affectedUri()).isSameAs(affectedUri);
        assertThat(updateResult.insertedUri()).isNull();
    }

    @Test
    public void shouldAllowCreatingUpdateResultWith0RowsUpdated() {
        PutResult putResult = PutResult.newUpdateResult(0, Mockito.mock(Uri.class));
        assertThat(putResult.wasUpdated()).isFalse();
        assertThat(putResult.wasInserted()).isFalse();
        assertThat(putResult.numberOfRowsUpdated()).isEqualTo(Integer.valueOf(0));
    }

    @Test
    public void shouldNotCreateUpdateResultWithNegativeNumberOfRowsUpdated() {
        try {
            PutResult.newUpdateResult((-1), Mockito.mock(Uri.class));
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Number of rows updated must be >= 0");
        }
    }

    @Test
    public void shouldCreateUpdateResultWithOneRowUpdated() {
        PutResult.newUpdateResult(1, Mockito.mock(Uri.class));// no exceptions should occur

    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedUri() {
        try {
            // noinspection ConstantConditions
            PutResult.newUpdateResult(1, null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("affectedUri must not be null");
        }
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier.forClass(PutResult.class).allFieldsShouldBeUsed().withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2")).verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker.forClass(PutResult.class).check();
    }
}

