package com.example.multiupload;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private static final int REQUEST_IMAGE_PICK = 1;

    private ImageView imageViewToSetImage;

    private ImageView imageView1, imageView2, imageView3;
    private EditText nameEditText, addressEditText;
    private Uri imageUri1, imageUri2, imageUri3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        nameEditText = findViewById(R.id.nameEditText);
        addressEditText = findViewById(R.id.addressEditText);
    }

    public void selectImageFromGalleryOrCamera(View view) {
        int requestCode = REQUEST_IMAGE_PICK;
        if (view.getId() == R.id.imageView1) {
            imageViewToSetImage = imageView1;
        } else if (view.getId() == R.id.imageView2) {
            imageViewToSetImage = imageView2;
        } else if (view.getId() == R.id.imageView3) {
            imageViewToSetImage = imageView3;
        } else {
            // Handle other ImageViews or views if needed.
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                try {
                    Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageViewToSetImage.setImageBitmap(selectedImage);
                    if (imageViewToSetImage == imageView1) {
                        imageUri1 = imageUri;
                    } else if (imageViewToSetImage == imageView2) {
                        imageUri2 = imageUri;
                    } else if (imageViewToSetImage == imageView3) {
                        imageUri3 = imageUri;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void postDataUsingVolley(View view) {
        // Create a RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Define the URL for your server
        String url = "http://192.168.10.2/android/upload.php";

        // Read data from TextViews
        String name = nameEditText.getText().toString();
        String address = addressEditText.getText().toString();

        // Encode and add image as Base64 string
        String image1Base64 = imageUri1 != null ? convertImageToBase64(imageUri1) : "";
        String image2Base64 = imageUri2 != null ? convertImageToBase64(imageUri2) : "";
        String image3Base64 = imageUri3 != null ? convertImageToBase64(imageUri3) : "";

        // Create a StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response from the server
                        Toast.makeText(MainActivity.this, "Data sent successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(MainActivity.this, "Error sending data", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add your data to the request
                params.put("name", name);
                params.put("address", address);
                params.put("image1", image1Base64);
                params.put("image2", image2Base64);
                params.put("image3", image3Base64);
                return params;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            // Load the selected image and convert it to Base64
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}