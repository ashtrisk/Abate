package com.ashutosh.abatev1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.HashMap;


public class SignInActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
View.OnClickListener{

    // client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;
    final private String TAG = "SignInActivity";
    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN_CODE = 0;
    // Is there a ConnectionResult resolution in progress?
    private boolean mIsResolving = false;
    // Should we automatically resolve ConnectionResults when possible?
    private boolean mShouldResolve = false;
    private boolean mGPlusConnected = false;
//    TextView mTextView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(userAlreadySignedIn()){
            launchMainActivity();
        }
        launchMainActivity();       // for debugging by default launch Main remove it later

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
//        getFragmentManager().beginTransaction().add(R.id.relativeLayout_detail, new SignInFragment()).commit();

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        findViewById(R.id.button_sign_in).setOnClickListener(this);
//        mTextView = (TextView) findViewById(R.id.textView_sign_in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_sign_in) {
            onSignInClicked();
        }
    }
    private void onSignInClicked(){
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        if(isNetworkAvailable()) {
            mGoogleApiClient.connect();     // if network is available then only
                                            // try to connect to GPlus
            Toast.makeText(this, "Signing in to GPlus", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please turn on Internet Connection and Retry.", Toast.LENGTH_SHORT).show();
        }
        // Show a message to the user that we are signing in.
//        mStatusTextView.setText("Signing in");
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(this, "onStart() called", Toast.LENGTH_LONG).show();
//        if(isNetworkAvailable()) {
//            mGoogleApiClient.connect();     // invoking google api client returns void although if connection is successful
//            // onConnected() is called & onConnectionFailed() is called on failure.
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGPlusConnected) {
            Toast.makeText(this, "Disconnecting GPlus", Toast.LENGTH_LONG).show();
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected() i.e. connection to GPlus is successful" + bundle);
        mGPlusConnected = true;
        mShouldResolve = false;

        // Show the signed-in UI
//        showSignedInUI();
        Toast.makeText(this, "Sign-in successful.", Toast.LENGTH_SHORT).show();

        if(isNetworkAvailable()) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);       // get current person via n/w
            if (currentPerson != null) {
                String personName = currentPerson.getDisplayName();
                String personPhoto = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                String fullcrum = personName + "\n" + personGooglePlusProfile + "\n" + personPhoto + "\n" + email;
                Toast.makeText(this, fullcrum, Toast.LENGTH_LONG).show();

                // Step 1 - Store successfulSignIn = true in SharedPreferences
                // Step 2 - Launch a background D/B helper asynchronously and store all user data onto the DB
                // Step 3 - Launch a bg N/W helper asynchronously and send this result to the server
                // Step 4 - Launching the MainActivity to show user all the posts. - done
                HashMap<String, String> userData = new HashMap<>();
                userData.put("username", personName);
                userData.put("email", email);
                userData.put("password", "gplus");

                NetworkHelper2 helper = new NetworkHelper2(userData, this);
                helper.execute();           // execute the async task and store data onto the server
                storeSignInSuccess();
                launchMainActivity();       // launches the MainActivity to show user all posts, should be the last step.
            }
        } else {
            Toast.makeText(this, "Please turn on Internet Connection and Retry.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection is suspended temporarily. Please Retry.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Connection Failed, Retrying.", Toast.LENGTH_SHORT).show();

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {     // returns true if startResolutionForResult() will start any intents
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN_CODE);    // resolves an error by starting an intents requiring user interaction.
                    mIsResolving = true;
                    Toast.makeText(this, "Resolving connection errors, please wait", Toast.LENGTH_SHORT).show();
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    Toast.makeText(this, "Retrying, Please wait.", Toast.LENGTH_SHORT).show();
                    mIsResolving = false;
                    mGoogleApiClient.connect();     // retry connection
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
//                showErrorDialog(connectionResult);
                Toast.makeText(this, "Error getting User Info. Please Retry with another account. ", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show the signed-out UI
//            showSignedOutUI();
            Toast.makeText(this, "Signing Out. Please Retry. ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        Toast.makeText(this, "Obtaining SignIn Result", Toast.LENGTH_LONG).show();

        if (requestCode == RC_SIGN_IN_CODE) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }

    }

    public boolean isNetworkAvailable() {
        // checks for availability of active internet connection in the device
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean userAlreadySignedIn(){
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        boolean defaultValue = false;
        // get the sign-in status value from the shared preferences. Returns 'defaultValue' if pref. doesn't exist
        boolean userSignInStatus = pref.getBoolean(getString(R.string.successful_sign_in), defaultValue);
        return userSignInStatus;
    }
    private void storeSignInSuccess(){
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(getString(R.string.successful_sign_in), true);
        editor.commit();                            // don't forget to commit
    }
    private void launchMainActivity(){
        // Note : We don't need a MainActivity any more because we have shifted our code of Main to the NavDrawerActivity
        //          which is our new Main.
//        Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, NavDrawerActivity.class);
        // removes the current activity from the activity history stack and makes the launched activity the new root.
        // done so that we do not see the SignInActivity onBackPress() of user.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}