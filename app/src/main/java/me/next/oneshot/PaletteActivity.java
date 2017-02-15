package me.next.oneshot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import me.next.oneshot.views.PaletteView;

/**
 * Created by NeXT on 17/2/10.
 */

public class PaletteActivity extends AppCompatActivity {

    PaletteView paletteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        paletteView = (PaletteView) findViewById(R.id.palette_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_palette, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.redo:
                paletteView.redo();
                break;
            case R.id.undo:
                paletteView.undo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
