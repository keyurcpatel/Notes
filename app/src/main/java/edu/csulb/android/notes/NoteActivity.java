package edu.csulb.android.notes;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import edu.csulb.android.notes.ExtraClass.DatabaseHelper;

import static edu.csulb.android.notes.ExtraClass.DatabaseHelper.COLUMN_CAPTION;
import static edu.csulb.android.notes.ExtraClass.DatabaseHelper.COLUMN_IMAGE_PATH;

public class NoteActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
    ImageView noteImageView;
    TextView noteCaption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteImageView = (ImageView) findViewById(R.id.note_review_image);
        noteCaption = (TextView) findViewById(R.id.note_review_caption);
        String ID = getIntent().getStringExtra("ID");
        Cursor result = mDatabaseHelper.getImagePath(ID);
        if (result.getCount() == 1) {
            result.moveToFirst();
            String caption = result.getString(result.getColumnIndex(COLUMN_CAPTION));
            String path = result.getString(result.getColumnIndex(COLUMN_IMAGE_PATH));
            System.out.println(caption+path);
            if(path != null)
            {
                noteImageView.setImageBitmap(rotateImage(path));
//              noteImageView.setImageURI(Uri.parse(path));
            }
            noteCaption.setText(caption);
        }
    }

    private Bitmap rotateImage(String imagePath) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bounds);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(imagePath, opts);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        return rotatedBitmap;
    }
}
