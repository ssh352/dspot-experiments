package com.firebase.ui.auth.viewmodel;


import android.arch.lifecycle.Observer;
import com.firebase.ui.auth.FirebaseAuthAnonymousUpgradeException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.testhelpers.AutoCompleteTask;
import com.firebase.ui.auth.testhelpers.FakeAuthResult;
import com.firebase.ui.auth.testhelpers.ResourceMatchers;
import com.firebase.ui.auth.testhelpers.TestHelper;
import com.firebase.ui.auth.viewmodel.phone.PhoneProviderResponseHandler;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link PhoneProviderResponseHandler}.
 */
@RunWith(RobolectricTestRunner.class)
public class PhoneProviderResponseHandlerTest {
    @Mock
    FirebaseAuth mMockAuth;

    @Mock
    FirebaseUser mMockUser;

    @Mock
    PhoneAuthCredential mCredential;

    @Mock
    Observer<Resource<IdpResponse>> mResponseObserver;

    private PhoneProviderResponseHandler mHandler;

    @Test
    public void testSignIn_withValidCredentialAndNewUser_expectSuccess() {
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.signInWithCredential(mCredential)).thenReturn(AutoCompleteTask.forSuccess(FakeAuthResult.INSTANCE));
        IdpResponse response = build();
        mHandler.startSignIn(mCredential, response);
        Mockito.verify(mMockAuth).signInWithCredential(mCredential);
        Mockito.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isSuccess()));
    }

    @Test
    public void testSignIn_autoUpgradeAnonymousEnabledWithNewUser_expectSuccess() {
        mHandler.getOperation().observeForever(mResponseObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.getCurrentUser().linkWithCredential(mCredential)).thenReturn(AutoCompleteTask.forSuccess(FakeAuthResult.INSTANCE));
        IdpResponse response = build();
        mHandler.startSignIn(mCredential, response);
        Mockito.verify(mMockAuth.getCurrentUser()).linkWithCredential(mCredential);
        Mockito.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isSuccess()));
    }

    @Test
    public void testSignIn_autoUpgradeAnonymousEnabledWithExistingUser_expectMergeFailure() {
        mHandler.getOperation().observeForever(mResponseObserver);
        setupAnonymousUpgrade();
        FirebaseAuthUserCollisionException ex = new FirebaseAuthUserCollisionException("foo", "bar");
        TestHelper.setPrivateField(ex, FirebaseAuthUserCollisionException.class, AuthCredential.class, mCredential);
        Mockito.when(mMockAuth.getCurrentUser().linkWithCredential(mCredential)).thenReturn(AutoCompleteTask.<AuthResult>forFailure(ex));
        IdpResponse response = build();
        mHandler.startSignIn(mCredential, response);
        Mockito.verify(mMockAuth.getCurrentUser()).linkWithCredential(mCredential);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        ArgumentCaptor<Resource<IdpResponse>> resolveCaptor = ArgumentCaptor.forClass(Resource.class);
        inOrder.verify(mResponseObserver).onChanged(resolveCaptor.capture());
        FirebaseAuthAnonymousUpgradeException e = ((FirebaseAuthAnonymousUpgradeException) (resolveCaptor.getValue().getException()));
        assertThat(e.getResponse().getCredentialForLinking()).isNotNull();
    }
}

