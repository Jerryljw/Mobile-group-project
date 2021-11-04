package com.comp90018.proj2.ui.finder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.comp90018.proj2.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for finder
 */
public class FinderFragment extends Fragment {
    TabLayout mTableLayout;
    private ViewPager mViewPager;
    private MyAdapter adapter;
    private List<String> mTitle;
    private List<Fragment> mFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finder,null);
        mTableLayout=view.findViewById(R.id.finder_tab);
        mViewPager=view.findViewById(R.id.finder_pager);

        // set nested fragments title
        mTitle = new ArrayList<>();
        mTitle.add("Fauna");
        mTitle.add("Flora");

        //fragment list
        mFragment = new ArrayList<>();
        mFragment.add(new AnimalFinderFragment());
        mFragment.add(new PlantFinderFragment());

        // getChildFragmentManager to resolve child fragment not
        // displaying after back from other fragment
        adapter = new MyAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mTableLayout.setupWithViewPager(mViewPager);
        return view;

    }


    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Get the position of every Fragment
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return mFragment.get(position);
        }

        @Override
        public int getCount() {
            return mFragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitle.get(position);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

}