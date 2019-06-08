package com.example.store;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CreateProduct extends AppCompatActivity {

    public String productId;
    public String productNameText;
    public String productDescriptionText;
    public String productTypeText;
    public String productPriceText;
    public String productImageText;
    public String companyId;

    public EditText productName, productDescription, productPrice, productType;
    public ImageView productImage;
    public ProgressBar progressBar;

    public FirebaseAuth auth = null;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference, imageReference;
    StorageTask uploadTask;
    public Product newProduct;

    private static final int PICK_IMAGE_REQUEST = 1;
    public Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productName = (EditText) findViewById(R.id.product_name);
        productDescription = (EditText) findViewById(R.id.product_description);
        productPrice = (EditText) findViewById(R.id.product_price);
        productType = (EditText) findViewById(R.id.product_type);
        productImage = (ImageView) findViewById(R.id.product_image);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference("images");

        FirebaseUser currentUser = auth.getCurrentUser();
        companyId = currentUser.getUid();

        productId = databaseReference.push().getKey();
    }

    // Add photo to image view
    public void addPhoto(View view) {
        openFileChooser();
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(productImage);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addProduct(View view) {
        if (uploadTask != null && uploadTask.isInProgress() == true) {
            Toast.makeText(getApplicationContext(), "Please wait until upload photo", Toast.LENGTH_LONG).show();
        } else {
            uploadPhoto();
        }
    }

    public void createProduct() {
        if (checkText() == false) {

            productNameText = productName.getText().toString();
            productDescriptionText = productDescription.getText().toString();
            productPriceText = productPrice.getText().toString();
            productTypeText = productType.getText().toString();
            newProduct = new Product(productId, productNameText, productDescriptionText, productTypeText, productPriceText, productImageText);

            databaseReference.child(companyId).child("id").child(productId).setValue(newProduct);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(getApplicationContext(), "data entered", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "data failed", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void uploadPhoto() {
        if (imageUri != null) {

            imageReference = storageReference.child(productId + "." + getImageExtension(imageUri));

            uploadTask = imageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            StorageMetadata metadata = taskSnapshot.getMetadata();
                            Task<Uri> downloadUrl = imageReference.getDownloadUrl();
                            downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadLink = uri.toString();
                                    productImageText = downloadLink;
                                    createProduct();

                                }
                            });
                            Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload Failed" + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_LONG).show();
        }
    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public boolean checkText() {
        boolean empty = false;
        productNameText = productName.getText().toString();
        productDescriptionText = productDescription.getText().toString();
        productPriceText = productPrice.getText().toString();
        productTypeText = productType.getText().toString();

        if (productNameText.equals("") || productDescriptionText.equals("") || productPriceText.equals("") || productTypeText.equals("") ||
                productDescriptionText.length() < 15 || productNameText.length() < 3) {
            if (productNameText.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Product Name!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (productDescriptionText.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Product Description!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (productPriceText.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Product Price!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (productTypeText.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Product Type!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (productDescriptionText.length() < 15) {
                Toast.makeText(getApplicationContext(), "Please Enter Good Description!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (productNameText.length() < 3) {
                Toast.makeText(getApplicationContext(), "Please Company Name must be at least 3 letters!", Toast.LENGTH_LONG).show();
                empty = true;
            }
        }
        return empty;
    }

    public void removeProduct(View view) {
        imageReference = firebaseStorage.getReferenceFromUrl(productImageText);
        imageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference = FirebaseDatabase.getInstance().getReference().child(companyId).child("id");
                        databaseReference.child(productId).removeValue();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "No Product Created!", Toast.LENGTH_SHORT).show();
                    }
                });
        Intent intent = new Intent(CreateProduct.this, MyCompany.class);
        startActivity(intent);
    }
}
