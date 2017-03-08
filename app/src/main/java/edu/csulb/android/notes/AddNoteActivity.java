package edu.csulb.android.notes;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.csulb.android.notes.ExtraClass.DatabaseHelper;

import static android.os.Environment.DIRECTORY_PICTURES;

public class AddNoteActivity extends AppCompatActivity {

    private static final int CAMERA_ACCESS = 1515;

    DatabaseHelper mDatabaseHelper;



    private ImageView imageView;
    private EditText captionText;
    private Button doneButton;
    private Button captureImageButton;

    private String caption = "";
    private String imagePath = "";
    private String thumbnailPath = "";

    private Bitmap rotatedBitmap;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        outState.putParcelable("image", bitmap);
        outState.putString("caption", captionText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        imageView.setImageBitmap((Bitmap) savedInstanceState.getParcelable("image"));
        captionText.setText(savedInstanceState.getString("caption"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    private Bitmap bitmap;
    private Bitmap thumbnailBitmap;

    private Intent callerIntent = getIntent();
    private String mCurrentPhotoPath;

    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        imageView = (ImageView) findViewById(R.id.imageView);
        captionText = (EditText) findViewById(R.id.captionText);
        doneButton = (Button) findViewById(R.id.doneButton);
        captureImageButton = (Button) findViewById(R.id.captureImageButton);
        mDatabaseHelper = new DatabaseHelper(this);
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "edu.csulb.android.notes.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_ACCESS);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    public void saveNote(View view) {

        imagePath = saveToInternalStorage(bitmap, false);
        thumbnailPath = saveToInternalStorage(thumbnailBitmap, true);
        if(captionText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Caption can not be empty.", Toast.LENGTH_LONG).show();
        }
        else {
            insertNote(captionText.getText().toString(), thumbnailPath, mCurrentPhotoPath);
        }

    }

    private void insertNote(String caption, String thumnailPath, String imagePath) {
        boolean result = mDatabaseHelper.insertData(caption, thumnailPath, imagePath);
        if(result == true) {
            setResult(RESULT_OK, callerIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_ACCESS && resultCode == RESULT_OK) {
            imageView.setImageBitmap(rotateImage(mCurrentPhotoPath));
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, boolean isThumbnail) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory;
        File mypath;
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Timestamp(System.currentTimeMillis()));
        if(isThumbnail) {
            // path to /data/data/yourapp/app_data/thumbnailDir
            directory = cw.getDir("thumbnailDir", Context.MODE_PRIVATE);
        }
        else {
            // path to /data/data/yourapp/app_data/imageDir
            directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        }

        mypath=new File(directory,timeStamp+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath()+"/"+timeStamp+".jpg";
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
        rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        return rotatedBitmap;
    }
}
