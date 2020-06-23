package com.jackdonaldson.majorwork2019.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jackdonaldson.majorwork2019.fragment.ChatFragment;
import com.jackdonaldson.majorwork2019.fragment.ProfileFragment;
import com.jackdonaldson.majorwork2019.fragment.SearchFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        switch(position){
            case 0:
                return SearchFragment.create();
            case 1:
                return ChatFragment.create();
            case 2:
                return ProfileFragment.create();
        }
        return null;
    }

    @Override
    public int getCount(){
        return 3;
    }
}
