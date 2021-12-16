package com.amstrong.gaseapp.ui.waiter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, private val tabTitles: List<String>, private val fragments: List<Fragment>)
    : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PlaceholderFragment.newInstance(position + 1)
        return fragments[position]
    }


    override fun getPageTitle(position: Int): CharSequence? {
//        return context.resources.getString(TAB_TITLES[position])
        return tabTitles[position]
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return fragments.size
    }
}