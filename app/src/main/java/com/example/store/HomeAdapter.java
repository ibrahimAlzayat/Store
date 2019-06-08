package com.example.store;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ProductViewHolder> {
    private List<Product> products;
    private Context context;
    private HomeAdapter.OnItemClickListener mListener;

    public HomeAdapter(Context context, List<Product> products) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeAdapter.ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View cv = LayoutInflater.from(context).inflate(R.layout.product_item, viewGroup, false);
        return new HomeAdapter.ProductViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ProductViewHolder productViewHolder, int position) {
        Product currentPro = products.get(position);
        String proName = currentPro.productName;
        String proImage = currentPro.productImage;

        View cardView = productViewHolder.cardView;
        ImageView productImage = (ImageView) cardView.findViewById(R.id.product_image_card_view);
        TextView productName = (TextView) cardView.findViewById(R.id.product_name_card_view);

        productName.setText(proName);
        Picasso.get().load(proImage).placeholder(R.mipmap.ic_launcher_round).fit().centerCrop().into(productImage);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView cardView;

        public ProductViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }


    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(HomeAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }
}
