package com.magicalrice.project.library_common.base

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.magicalrice.project.library_base.base.AppUtils
import com.magicalrice.project.library_common.R
import com.magicalrice.project.library_widget.base.TabIconLayout
import com.magicalrice.project.library_widget.bean.TabViewBean

abstract class BaseHomeActivity<V : ViewDataBinding, VM : BaseViewModel> : BaseActivity<V,VM>() {
    private var fragment: Fragment? = null
    private var currentFragment: Fragment? = null
    private var bottomNavView: TabLayout? = null
    private lateinit var fm: FragmentManager
    private lateinit var tabList: ArrayList<TabViewBean>

    protected fun initTabLayout(@IdRes id: Int,tabList: ArrayList<TabViewBean>) {
        this.tabList = tabList
        bottomNavView = findViewById(id)

        bottomNavView?.run {
            tabList.forEach {
                addTab(newTab().setCustomView(getTabView(it.title, it.drawableId)).setTag(it.tag))
            }

            addOnTabSelectedListener(object :
                TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
                override fun onTabReselected(p0: TabLayout.Tab?) {
                    p0?.customView?.isSelected = true
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                    p0?.customView?.isSelected = false
                }

                override fun onTabSelected(p0: TabLayout.Tab?) {
                    p0?.customView?.isSelected = true
                    changeFragment(p0?.tag as String)
                }
            })
            initContent()
        }

    }

    private fun initContent() {
        fm = supportFragmentManager
        changeFragment("home")
    }

    private fun changeFragment(tag: String) {
        fragment = fm.findFragmentByTag(tag)
        val ft = fm.beginTransaction()
        if (fragment == null) {
            getFragmentByTag(tag)

            if (currentFragment != null) {
                ft.hide(currentFragment!!)
            }

            ft.add(R.id.home_content,fragment!!,tag)
            ft.commitAllowingStateLoss()
            currentFragment = fragment
        } else {
            if (currentFragment != null) {
                ft.hide(currentFragment!!)
            }
            ft.show(fragment!!)
            currentFragment = fragment
            ft.commitAllowingStateLoss()
        }
    }

    private fun getFragmentByTag(tag: String) {
        val className = tabList.filter {
            it.tag == tag
        }.firstOrNull()?.fragmentName

        className?.let {
            fragment = Class.forName(className).newInstance() as Fragment
        }
    }

    // Tab自定义view
    private fun getTabView(title: String, @DrawableRes image_src: Int): View {
        val v = TabIconLayout(this)
        v.setTabTitle(title)
        v.setTabIcon(image_src)
        return v
    }

    override fun onBackPressed() {
        if (vertifyClickTime()) {
            showToast("再按一次退出")
        } else {
            AppUtils.exitApp()
            super.onBackPressed()
        }
    }
}