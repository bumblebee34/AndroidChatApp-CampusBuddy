package com.example.bhagyarajapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ClubPosterFragment clubPosterFragment = new ClubPosterFragment();
                return  clubPosterFragment;
            case 1:
                ClubDescriptionFragment clubDescriptionFragment = new ClubDescriptionFragment();
                return clubDescriptionFragment;
            case 2:
                ClubVenueFragment clubVenueFragment = new ClubVenueFragment();
                return clubVenueFragment;
            case 3:
                ClubContactFragment clubContactFragment = new ClubContactFragment();
                return  clubContactFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Poster";
            case 1:
                return "Description";
            case 2:
                return "Venue";
            case 3:
                return "Contact";
            default:
                return null;
        }
    }
}
