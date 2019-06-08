package com.example.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class HomeActivity extends AppCompatActivity implements HomeAdapter.OnItemClickListener {

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    FirebaseUser currentUser;
    StorageReference storageReference, imageReference;
    List<Product> productsList;
    List<Company> companiesList;
    HomeAdapter adapter;
    String companyId;
    ValueEventListener valueEventListener;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));

        progressBar = (ProgressBar) findViewById(R.id.progress_circular_home);

        productsList = new ArrayList();
        companiesList = new ArrayList();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HomeAdapter(HomeActivity.this, productsList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(HomeActivity.this);

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
                companiesList.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    Company company = children.getValue(Company.class);
                    companiesList.add(0,company);

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Product product = child.getValue(Product.class);
                        productsList.add(0, product);
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }


    @Override
    public void onItemClick(int position) {
        showActivity(position);
    }

    public void showActivity(int position) {
        Product product = productsList.get(position);
        Intent intent = new Intent(HomeActivity.this, ShowActivity.class);
        intent.putExtra("name", product.productName);
        intent.putExtra("description", product.productDescription);
        intent.putExtra("image", product.productImage);
        intent.putExtra("price", product.productPrice);
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
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }
}
