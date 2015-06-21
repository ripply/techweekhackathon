package com.example.lpoon2.camera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.File;


public class MainActivity extends ActionBarActivity {
    public static class App {
        public static File _file;
        public static File _dir;
        public static Bitmap bitmap;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        base.OnCreate(bundle);
        SetContentView(Resource.Layout.Main);

        if (IsThereAnAppToTakePictures ())
        {
            CreateDirectoryForPictures ();

            Button button = FindViewById<Button>(Resource.Id.myButton);
            _imageView = FindViewById<ImageView>(Resource.Id.imageView1);
            button.Click += TakeAPicture;
        }
    }

    private void CreateDirectoryForPictures ()
    {
        App._dir = new File (
                Environment.GetExternalStoragePublicDirectory(
                        Environment.DirectoryPictures), "CameraAppDemo");
        if (!App._dir.Exists ())
        {
            App._dir.Mkdirs( );
        }
    }

    private bool IsThereAnAppToTakePictures ()
    {
        Intent intent = new Intent (MediaStore.ActionImageCapture);
        IList<ResolveInfo> availableActivities =
                PackageManager.QueryIntentActivities(intent, PackageInfoFlags.MatchDefaultOnly);
        return availableActivities != null && availableActivities.Count > 0;
    }
    private void TakeAPicture (object sender, EventArgs eventArgs)
    {
        Intent intent = new Intent (MediaStore.ActionImageCapture);
        App._file = new File (App._dir, String.Format("myPhoto_{0}.jpg", Guid.NewGuid()));
        intent.PutExtra (MediaStore.ExtraOutput, Uri.FromFile(App._file));
        StartActivityForResult (intent, 0);
    }
    protected override void OnActivityResult (int requestCode, Result resultCode, Intent data)
    {
        base.OnActivityResult (requestCode, resultCode, data);

        // Make it available in the gallery

        Intent mediaScanIntent = new Intent (Intent.ActionMediaScannerScanFile);
        Uri contentUri = Uri.FromFile (App._file);
        mediaScanIntent.SetData (contentUri);
        SendBroadcast (mediaScanIntent);

        // Display in ImageView. We will resize the bitmap to fit the display.
        // Loading the full sized image will consume to much memory
        // and cause the application to crash.

        int height = Resources.DisplayMetrics.HeightPixels;
        int width = _imageView.Height ;
        App.bitmap = App._file.Path.LoadAndResizeBitmap (width, height);
        if (App.bitmap != null) {
            _imageView.SetImageBitmap (App.bitmap);
            App.bitmap = null;
        }

        // Dispose of the Java side bitmap.
        GC.Collect();
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
