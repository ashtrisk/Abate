package com.ashutosh.abatev1;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private int backPress = 0;           // determines no. of back pressed
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == CreatePostFragment.ITEM_INDEX_IN_DRAWER) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new CreatePostFragment()).commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.category_section);
                break;
            case 2:
                mTitle = getString(R.string.new_post_section);
                break;
            case 3:
                mTitle = getString(R.string.about_section);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(backPress < 1) {
            backPress++;
            Toast.makeText(this, "Tap back once more to exit application", Toast.LENGTH_SHORT).show();
        } else {
            finish();
//            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.nav_drawer, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            /*  View rootView = inflater.inflate(R.layout.fragment_nav_drawer, container, false);
            Context ctx = getActivity();

            RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_navActivityFragment);

            LinearLayoutManager llm = new LinearLayoutManager(ctx);
            recyclerView.setLayoutManager(llm);

            XRecyclerAdapter adapter = new XRecyclerAdapter();
            recyclerView.setAdapter(adapter);   */

            View rootView = inflater.inflate(R.layout.fragment_card_view, container, false);
            Context ctx = getActivity();

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_cardViewFragment);
            LinearLayoutManager llm = new LinearLayoutManager(ctx);
            recyclerView.setLayoutManager(llm);         // adapter will be set in the UIHelper class
            recyclerView.setHasFixedSize(true);

            if (savedInstanceState == null) {
                UIHelper uiHelper = new UIHelper(ctx, rootView);
                uiHelper.execute();
            }

//            if(savedInstanceState!=null){
//                UIHelper uiHelper = new UIHelper(ctx, rootView);
//                uiHelper.execute();
//            }

            return rootView;
        }

        @Override
        public void onAttach (Activity activity){
            super.onAttach(activity);
            ((NavDrawerActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}