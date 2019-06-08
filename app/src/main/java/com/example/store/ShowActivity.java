package com.example.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ShowActivity extends AppCompatActivity {

    public TextView productName, productDescription;
    public Button productPrice;
    public ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));

        productName = (TextView) findViewById(R.id.name);
        productDescription = (TextView) findViewById(R.id.description);
        productPrice = (Button) findViewById(R.id.price);
        productImage = (ImageView) findViewById(R.id.image);


        Intent intent = getIntent();
        productName.setText(intent.getStringExtra("name"));
        productDescription.setText(intent.getStringExtra("description"));
        productPrice.setText(intent.getStringExtra("price")+ " $");

        String image = intent.getStringExtra("image");
        Picasso.get().load(image).placeholder(R.mipmap.ic_launcher_round).fit().centerCrop().into(productImage);
    }
}
