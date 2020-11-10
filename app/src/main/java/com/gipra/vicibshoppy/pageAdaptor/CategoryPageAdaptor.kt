package com.gipra.vicibshoppy.pageAdaptor

import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class CategoryPageAdaptor(fm: FragmentManager) : FragmentStatePagerAdapter(fm){



    private val tabNames: ArrayList<String>
    private val fragments: ArrayList<Fragment>

    init {
        tabNames = ArrayList()
        fragments = ArrayList()
    }


    override fun getItem(position: Int): Fragment {
        return fragments.get(position)
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabNames.get(position)
    }

    fun addFragment(
        fragment: Fragment,
        title: String
    ){
        fragments.add(fragment)
        tabNames.add(title)

    }
}
