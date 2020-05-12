package com.example.easydiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class CreateFragment extends Fragment {

    final int REQUEST_IMAGE_CAPTURE = 1001;

    //
    private EditText txtTitle;
    private EditText txtDesc;
    private ImageView imgCancel;
    private ImageView imgCamera;

    private boolean hasImage;
    private byte[] image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        //text field
        txtTitle = view.findViewById(R.id.txtTitle);
        txtDesc = view.findViewById(R.id.txtDesc);

        //camera
        hasImage = false;
        image = null;
        imgCancel = view.findViewById(R.id.imgCancel);
        imgCamera = view.findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });
        imgCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                image = null;
                imgCamera.setImageResource(R.drawable.baseline_add_a_photo_black_24dp);
                hasImage = false;
            }
        });

        return view;
    }

    //==================================================================================================================
    //--- camera
    //==================================================================================================================
    //from:
    //  stackoverflow.com/questions/11274715/save-bitmap-to-file-function/39116563
    @Override //called on finish taking photo from camere
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                image = stream.toByteArray();
                hasImage = true;

                imgCamera.setImageBitmap(bitmap);
                Toast.makeText(getActivity(),""+image, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //==================================================================================================================
    //--- audio
    //==================================================================================================================



}
