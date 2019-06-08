package com.example.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MyCompany extends AppCompatActivity implements CompanyProductsAdapter.OnItemClickListener {

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    FirebaseUser currentUser;
    StorageReference storageReference, imageReference;
    List<Product> productsList;
    CompanyProductsAdapter adapter;
    String companyId;
    ValueEventListener valueEventListener;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_company);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));

        progressBar = (ProgressBar) findViewById(R.id.progress_circular);

        productsList = new ArrayList();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CompanyProductsAdapter(MyCompany.this, productsList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(MyCompany.this);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        currentUser = auth.getCurrentUser();
        companyId = currentUser.getUid();

        storageReference = FirebaseStorage.getInstance().getReference("images");
        databaseReference = FirebaseDatabase.getInstance().getReference().child(companyId).child("id");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productsList.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    Product product = children.getValue(Product.class);
                    productsList.add(0, product);
                }
                progressBar.setVisibility(View.INVISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyCompany.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProductActivity();
            }
        });


    }

    public void createProductActivity() {
        Intent intent = new Intent(MyCompany.this, CreateProduct.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MyCompany.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        showActivity(position);
    }

    @Override
    public void onShowClick(int position) {
        showActivity(position);
    }

    @Override
    public void onUpdateClick(int position) {
        updateActivity(position);
    }

    @Override
    public void onDeleteClick(int position) {
        Product product = productsList.get(position);
        final String key = product.id;
        imageReference = firebaseStorage.getReferenceFromUrl(product.productImage);
        imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(key).removeValue();
                Toast.makeText(MyCompany.this, "Item Deleted", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void showActivity(int position) {
        Product product = productsList.get(position);
        Intent intent = new Intent(MyCompany.this, ShowActivity.class);
        intent.putExtra("name", product.productName);
        intent.putExtra("description", product.productDescription);
        intent.putExtra("image", product.productImage);
        intent.putExtra("price", product.productPrice);
        startActivity(intent);
    }

    public void updateActivity(int position) {
        Product product = productsList.get(position);
        Intent intent = new Intent(MyCompany.this, UpdateProduct.class);
        intent.putExtra("id", product.id);
        intent.putExtra("name", product.productName);
        intent.putExtra("description", product.productDescription);
        intent.putExtra("image", product.productImage);
        intent.putExtra("price", product.productPrice);
        intent.putExtra("type", product.productType);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }
}
