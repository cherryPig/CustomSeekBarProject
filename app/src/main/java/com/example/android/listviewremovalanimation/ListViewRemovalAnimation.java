/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.listviewremovalanimation;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import best.customseekbarproject.R;

/**
 * This example shows how to use a swipe effect to remove items from a ListView,
 * and how to use animations to complete the swipe as well as to animate the other
 * items in the list into their final places.
 * <p/>
 * Watch the associated video for this demo on the DevBytes channel of developer.android.com
 * or on YouTube at https://www.youtube.com/watch?v=NewCSg2JKLk.
 */
public class ListViewRemovalAnimation extends Activity {

    StableArrayAdapter mAdapter;
    RecyclerView mListView;
    BackgroundContainer mBackgroundContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_deletion);

        mBackgroundContainer = (BackgroundContainer) findViewById(R.id.listViewBackground);
        mListView = (RecyclerView) findViewById(R.id.listview);
        mListView.setLayoutManager(new LinearLayoutManager(this));

        final ArrayList<String> cheeseList = new ArrayList<String>();
        for (int i = 0; i < Cheeses.sCheeseStrings.length; ++i) {
            cheeseList.add(Cheeses.sCheeseStrings[i]);
        }
        mAdapter = new StableArrayAdapter(cheeseList,mBackgroundContainer);
        mListView.setAdapter(mAdapter);
    }


}
