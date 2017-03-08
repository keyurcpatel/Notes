package edu.csulb.android.notes;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import edu.csulb.android.notes.ExtraClass.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;

    private static final int SAVE_NOTE = 1010;

    SimpleCursorAdapter simpleCursorAdapter;
    Dialog dialog;

    TextView caption_text;

    Bitmap imageBitmap;
    Bitmap thumbnailBitmap;

    ImageView note_image;

    ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDatabaseHelper = new DatabaseHelper(this);
        listView = (ListView) findViewById(R.id.listView);

        showNotes();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout note_layout = (LinearLayout) view;
                System.out.println(note_layout.getChildCount());
                TextView note_ID = (TextView) note_layout.getChildAt(0);
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("ID", note_ID.getText());
                startActivity(intent);
            }
        });
    }

    public void showNotes() {
        Cursor result = mDatabaseHelper.getAllData();
        if(result == null){
        }
        if(result.getCount()==0){
            Toast.makeText(getApplicationContext(), "Press + button to begin...", Toast.LENGTH_LONG).show();
        }

        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_CAPTION
        };

        int[] to = new int[] {
                R.id.row_id,
                R.id.row_Caption
        };


        try{
            simpleCursorAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.note_row,
                    result,
                    columns,
                    to,
                    0);

            listView.setAdapter(simpleCursorAdapter);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public void addNote(View view) {
        Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
        startActivityForResult(intent, SAVE_NOTE);

        //mDatabaseHelper.insertData("This is Caption", "This is Thumbnail path", "This is Image path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SAVE_NOTE && resultCode == RESULT_OK) {
            Cursor result = mDatabaseHelper.getAllData();
            simpleCursorAdapter.changeCursor(result);
            simpleCursorAdapter.notifyDataSetChanged();
        }
    }

    private void loadImageFromStorage(String path) {

        try {
            File f=new File(path, "");
            thumbnailBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            String deleteThis = "" ;
            //Todo: Change imageview in onItemClickListener of listview to load the photo
//            ImageView img=(ImageView)findViewById(R.id.note_image);
//            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
    private void showDialog() {
        // custom dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.note);

        // set the custom dialog components - text, image and button
        ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);
        note_image.setImageBitmap(thumbnailBitmap);

        // Close Button
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //TODO Close button action
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uninstallOption:
                uninstall();
                return true;
            case R.id.deleteAll:
                mDatabaseHelper.deleteAll();
                Cursor result = mDatabaseHelper.getAllData();
                simpleCursorAdapter.changeCursor(result);
                simpleCursorAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void uninstall() {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        startActivity(intent);
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        }


}

