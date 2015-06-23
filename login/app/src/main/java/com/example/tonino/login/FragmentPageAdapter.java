package com.example.tonino.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentPageAdapter  extends FragmentStatePagerAdapter{

    private int TOTAL_TABS = 3;
    private FragmentManager fM;

    public FragmentPageAdapter(FragmentManager fm) {
        super(fm);
        fM = fm;
        // TODO Auto-generated constructor stub
    }

    //List<Fragment> lista = new ArrayList<Fragment>();

    @Override
    public Fragment getItem(int index) {
        // TODO Auto-generated method stub
        Fragment fragment;
        //fragment = fM.findFragmentById(R.id.tabspager);
        //if(fragment == null){
        switch (index) {
            case 0:
                //if(Premio.class.getName().equals(fragment.getClass().getName()))
                    return new Vicinanza();
            case 1:
                    return new Consigliati();
            case 2:
                    return new Ricerca();
        }

        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return TOTAL_TABS;
    }

}