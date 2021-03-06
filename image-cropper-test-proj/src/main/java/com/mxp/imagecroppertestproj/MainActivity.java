package com.mxp.imagecroppertestproj;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mxp.profileimagecropper.ProfileImageCropper;
import com.mxp.profileimagecropper.ProfileImageCropperActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private static final int PICK_IMAGE_FROM_GALLERY = 9000;
  private static final int PICA_ACITIVTY = 9001;
  ProfileImageCropper image = null;

  Button loadNew = null;
  Button cropImage = null;
  Button launchActivity = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    image = ((ProfileImageCropper) findViewById(R.id.profileImage));

    initCropImage();
    initDrawBorder();
    initLoadNewButton();
  }

  private void initDrawBorder() {
    launchActivity = (Button) findViewById(R.id.activity);
    launchActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // image.setDrawBorder(!image.isDrawBorder());

        Intent intent = new Intent(getBaseContext(), ProfileImageCropperActivity.class);
        intent.putExtra("cropperBackground", Color.argb(100, 250, 190, 30));
        intent.putExtra("cropperBorder", Color.argb(255, 223, 133, 07));
        intent.putExtra("cropperBorderWidth", 5);
        intent.putExtra("cropperWidth", 350);
        intent.putExtra("cropperMinimumWidth", 200);
        intent.putExtra("handleBackground", Color.argb(255, 223, 133, 07));
        intent.putExtra("handleBorder", Color.argb(255, 223, 133, 07));
        intent.putExtra("handleBorderWidth", 5);
        intent.putExtra("handleWidth", 65);
        intent.putExtra("background", Color.argb(255, 0, 0, 0));
        intent.putExtra("controlBackground", Color.argb(255, 0, 0, 0));

        startActivityForResult(intent, PICA_ACITIVTY);
      }
    });
  }

  private void initCropImage() {
    cropImage = (Button) findViewById(R.id.cropImage);
    cropImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try {
          Bitmap bmp = ((ProfileImageCropper) findViewById(R.id.profileImage)).crop();
          image.setEditMode(false);
          image.setImageBitmap(bmp);
        } catch (Exception e) {
          Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
            .setActionTextColor(getResources().getColor(android.R.color.primary_text_dark))
            .show();
        }
      }
    });
  }

  private void initLoadNewButton() {
    loadNew = (Button) findViewById(R.id.loadNew);
    loadNew.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_FROM_GALLERY);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
      handleFromGallery(data);
    } else if (requestCode == PICA_ACITIVTY && resultCode == RESULT_OK) {
      handleFromPICA(data); // profile image cropper activity
    }
  }

  private void handleFromPICA(Intent data) {
    if (data == null) return;

    // use file in filename, and then store it, move it, or delete it, it's in a temporary folder.
    String fileName = data.getStringExtra("result");

    try {
      File f = new File(fileName);
      Picasso.with(this).load(f).fit().centerInside().into(image);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void handleFromGallery(Intent data) {
    if (data == null) return;

    try {
      InputStream inputStream = getBaseContext().getContentResolver().openInputStream(data.getData());
      Picasso.with(getBaseContext()).load(data.getData()).fit().centerInside().into(image);
      image.setEditMode(true);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
