package com.comp90018.proj2.ui.finder;
import com.comp90018.proj2.data.model.CardItem;
import com.comp90018.proj2.ui.post.*;
import android.os.Bundle;
import com.comp90018.proj2.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.comp90018.proj2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class FinderFragment extends Fragment {
    TabLayout mTableLayout;
    private ViewPager mViewPager;
    private MyAdapter adapter;
    private List<String> mTitle;
    private List<Fragment> mFragment;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //绑定一下fragment资源文件以及生成fragment对象
        View view = inflater.inflate(R.layout.fragment_finder,null);


        mTableLayout=view.findViewById(R.id.finder_tab);
        mViewPager=view.findViewById(R.id.finder_pager);
        //标题栏数组
        mTitle = new ArrayList<>();
        mTitle.add("Fauna");
        mTitle.add("Flora");

        //fragment集合
        mFragment = new ArrayList<>();
        mFragment.add(new AnimalFinderFragment());
        mFragment.add(new PlantFinderFragment());

        // getChildFragmentManager to resolve child fragment not displaying after back from other
        // fragment
        adapter = new MyAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        //将TabLayout和ViewPager绑定在一起，一个动另一个也会跟着动
        mTableLayout.setupWithViewPager(mViewPager);
        //返回视图
        return view;

    }
    //创建Fragment的适配器
    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }
        //获得每个页面的下标
        @Override
        public Fragment getItem(int position) {
            return mFragment.get(position);
        }
        //获得List的大小
        @Override
        public int getCount() {
            return mFragment.size();
        }
        //获取title的下标
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