package com.example.imagepickerlikewhatsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.male) RadioButton maleRadioButton;
    @BindView(R.id.female) RadioButton femaleButton;
    @BindView(R.id.title) TextView toolbarTitle;
    @BindView(R.id.userName) TextInputEditText userName;
    @BindView(R.id.userAddress) TextInputEditText userAddress;
    @BindView(R.id.userLicence) TextInputEditText userLicence;
    private int REQUEST_CODE = 100;
    private String userType;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            toolbarTitle.setText(R.string.registration);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        maleRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    femaleButton.setChecked(false);
                    userType = getString(R.string.male);
                }
            }
        });

        femaleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    maleRadioButton.setChecked(false);
                    userType = getString(R.string.female);

                }
            }
        });
    }

    @OnClick(R.id.upload_photo)
    void uploadPhoto(){
        Pix.start(this, REQUEST_CODE, 1);
    }

    @OnClick(R.id.next)
    void completeRegistration(){
        String name = userName.getText().toString().trim();
        String address = userAddress.getText().toString().trim();
        String licence = userLicence.getText().toString().trim();
        startActivity(new Intent(this, RegistrationCompleteActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            if(!returnValue.isEmpty()) {
                setImage(returnValue.get(0));
            }
        }
    }

    private void setImage(String imageUrl){
        imageUri = Uri.fromFile(new File(imageUrl));
        Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.ic_user_placeholder)
                        .error(R.drawable.ic_user_placeholder))
                .into(profileImage);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, REQUEST_CODE,1);
                } else {
                    Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
