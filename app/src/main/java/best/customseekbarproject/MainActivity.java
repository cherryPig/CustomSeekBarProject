package best.customseekbarproject;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.android.listviewremovalanimation.Cheeses;
import com.example.recyclerview.itemtouchhelper.CustomTouchListener;
import com.example.recyclerview.itemtouchhelper.ItemHelperCallBack;
import com.example.recyclerview.itemtouchhelper.StableArrayAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MusicSeekBar musicSeekBar;
    private RecyclerView mRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        itemHelper();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void itemHelper(){
        final ArrayList<String> cheeseList = new ArrayList<String>();
        for (int i = 0; i < Cheeses.sCheeseStrings.length; ++i) {
            cheeseList.add(Cheeses.sCheeseStrings[i]);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        StableArrayAdapter adapter = new StableArrayAdapter(cheeseList);
        mRecyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemHelperCallBack(adapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void music() {
        try {
            AssetManager am = getAssets();
            AssetFileDescriptor afd = am.openFd("delivery.mp3");
            musicSeekBar.setMediaSource(afd.getFileDescriptor());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
