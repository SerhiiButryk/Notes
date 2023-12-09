/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.fragments

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.serhii.apps.notes.R
import com.serhii.core.log.Log

open class BaseFragment : Fragment() {

    private var searchView: SearchView? = null
    private var searchActionView: MenuItem? = null

    /**
     * This should reliably check when the fragment is shown at the top of the back stack
     */
    protected fun isFragmentAtTheTop(frTag: String): Boolean {
        val fm = activity?.supportFragmentManager
        if (fm != null) {
            val myFragmentList = fm.fragments
            if (myFragmentList.isNotEmpty()) {

                val lastFragment = myFragmentList.last()

                if (lastFragment != null && lastFragment.tag == frTag && lastFragment.isVisible) {
                    Log.info("BaseFragment", "isFragmentVisible() fragment $frTag visible = true")
                    return true
                }
            }

        }
        Log.info("BaseFragment", "isFragmentVisible() fragment $frTag visible = false")
        return false
    }

    protected fun setupSearchInterface(menu: Menu) {
        searchActionView = menu.findItem(R.id.search)
        searchView = searchActionView?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.detail("BaseFragment", "onQueryTextSubmit() $query")
                // Issue: Search is done multiple times without this change
                // See details here: https://stackoverflow.com/questions/34207670/the-onquerytextsubmit-in-searchview-is-processed-twice-in-android-java
                searchView?.clearFocus()
                if (query != null) {
                    onSearchStarted(query)
                } else {
                    Log.error("BaseFragment", "onQueryTextSubmit() query is null")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // If the query is empty, then it's because the Search View field is closed
                    // or User cleared entered query. In any case we should reset selection.
                    // So, calling the code to finish the search
                    onSearchFinished()
                    return true
                }
                return true
            }
        })
    }

    fun collapseSearchbar() {
        searchActionView?.collapseActionView()
    }

    open fun onSearchStarted(query: String) {}

    open fun onSearchFinished() {}

}