package com.drose.selfi.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.drose.selfi.AccountCallback;
import com.drose.selfi.AccountManager;
import com.drose.selfi.MainActivity;
import com.drose.selfi.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyCamera.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyCamera#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCamera extends BaseFragment implements Button.OnClickListener{
    // Activity result key for camera
    static final int REQUEST_TAKE_PHOTO = 11111;

    // Image view for showing our image.
    private ImageView mImageView;
    private ImageView mThumbnailImageView;

    /**
     * Default empty constructor.
     */
    public MyCamera(){
        super();
    }

    /**
     * Static factory method
     * @param sectionNumber
     * @return
     */
    public static MyCamera newInstance(int sectionNumber) {
        MyCamera fragment = new MyCamera();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * OnCreateView fragment override
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  null;
        view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Set the image view
        mImageView = (ImageView)view.findViewById(R.id.imageViewFullSized);
        mThumbnailImageView = (ImageView)view.findViewById(R.id.imageViewThumbnail);
        Button takePictureButton = (Button)view.findViewById(R.id.button);

        // Set OnItemClickListener so we can be notified on button clicks
        takePictureButton.setOnClickListener((View.OnClickListener) this);

        ImageButton goTwitter = (ImageButton)view.findViewById(R.id.imageButton);

        goTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                    String url = "http://mobile.twitter.com";
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
                CameraActivity activity = (CameraActivity)getActivity();
                if (activity.getCurrentPhotoPath() != null) {
                    File f = new File(activity.getCurrentPhotoPath());  //optional //internal storage
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "hi");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));  //optional//use this when you want to send an image
                    shareIntent.setType("image/jpeg");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(Intent.createChooser(shareIntent, "send"), 4444);
                }
            }
        });
        return view;
    }

    /**
     * Start the camera by dispatching a camera intent.
     */
    protected void dispatchTakePictureIntent() {

        // Check if there is a camera.
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Camera exists? Then proceed...
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        CameraActivity activity = (CameraActivity)getActivity();
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go.
            // If you don't do this, you may get a crash in some devices.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(activity, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                activity.setCapturedImageURI(fileUri);
                activity.setCurrentPhotoPath(fileUri.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        activity.getCapturedImageURI());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * The activity returns with the photo.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            addPhotoToGallery();
            CameraActivity activity = (CameraActivity)getActivity();

            // Show the full sized image.
            setFullImageFromFilePath(activity.getCurrentPhotoPath(), mImageView);
            setFullImageFromFilePath(activity.getCurrentPhotoPath(), mThumbnailImageView);
        } else if(requestCode == 4444 && resultCode == Activity.RESULT_OK) {
            AccountManager.getInstance().enter("123", MainActivity.beaconUuid, new AccountCallback() {
                @Override
                public void loginComplete(boolean success) {

                }

                @Override
                public void signupComplete(boolean success) {

                }

                @Override
                public void entryComplete(boolean success) {
                    final boolean finalSuccess = success;
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (finalSuccess) {
                                Toast.makeText(getActivity(), "You were entered in the drawing!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "An error occured :(", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });

        } else {
            Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Creates the image file to which the image must be saved.
     * @return
     * @throws IOException
     */
    protected File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        CameraActivity activity = (CameraActivity)getActivity();
        activity.setCurrentPhotoPath("file:" + image.getAbsolutePath());
        return image;
    }

    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     */
    protected void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        CameraActivity activity = (CameraActivity)getActivity();
        File f = new File(activity.getCurrentPhotoPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);
    }

    /**
     * Deal with button clicks.
     * @param v
     */
    //@Override
    public void onClick(View v) {
        dispatchTakePictureIntent();
    }

    /**
     * Scale the photo down and fit it to our image views.
     *
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }




}








