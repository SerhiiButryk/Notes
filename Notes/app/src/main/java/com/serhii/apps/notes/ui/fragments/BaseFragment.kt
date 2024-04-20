/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.serhii.apps.notes.R
import com.serhii.core.log.Log

open class BaseFragment(tag: String) : Fragment() {

    private var searchView: SearchView? = null
    private var searchActionView: MenuItem? = null

    private val TAG = "BaseFragment-$tag"

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
                    Log.info(TAG, "isFragmentVisible() fragment $frTag visible = true")
                    return true
                }
            }

        }
        Log.info(TAG, "isFragmentVisible() fragment $frTag visible = false")
        return false
    }

    protected fun setupSearchInterface(menu: Menu) {
        searchActionView = menu.findItem(R.id.search)
        searchView = searchActionView?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.detail(TAG, "onQueryTextSubmit() $query")
                // Issue: Search is done multiple times without this change
                // See details here: https://stackoverflow.com/questions/34207670/the-onquerytextsubmit-in-searchview-is-processed-twice-in-android-java
                searchView?.clearFocus()
                if (query != null) {
                    onSearchStarted(query)
                } else {
                    Log.error(TAG, "onQueryTextSubmit() query is null")
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

    override fun onAttach(context: Context) {
        Log.detail(TAG, "onAttach(context)")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.detail(TAG, "onCreate()")
    }

    override fun onResume() {
        Log.detail(TAG, "onResume()")
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.detail(TAG, "onCreateView()")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.detail(TAG, "onViewStateRestored()")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.detail(TAG, "onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        Log.detail(TAG, "onDestroyView()")
        super.onDestroyView()
    }

    override fun onStop() {
        Log.detail(TAG, "onStop()")
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.detail(TAG, "onSaveInstanceState()")
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        Log.detail(TAG, "onPause()")
        super.onPause()
    }

    override fun onDestroy() {
        Log.detail(TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.detail(TAG, "onDetach()")
        super.onDetach()
    }

}