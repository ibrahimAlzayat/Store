package com.example.store;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CompanyProductsAdapter extends RecyclerView.Adapter<CompanyProductsAdapter.ProductViewHolder> {
    private List<Product> products;
    private Context context;
    private OnItemClickListener mListener;

    public CompanyProductsAdapter(Context context, List<Product> products) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public CompanyProductsAdapter.ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View cv = LayoutInflater.from(context).inflate(R.layout.product_item, viewGroup, false);
        return new ProductViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyProductsAdapter.ProductViewHolder productViewHolder, int position) {
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

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public CardView cardView;

        public ProductViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view);
            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);
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

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem show = menu.add(Menu.NONE, 1, 1, "Show");
            MenuItem update = menu.add(Menu.NONE, 2, 2, "Update");
            MenuItem delete = menu.add(Menu.NONE, 3, 3, "Delete");

            show.setOnMenuItemClickListener(this);
            update.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            mListener.onItemClick(position);
                            mListener.onShowClick(position);
                            return true;
                        case 2:
                            mListener.onUpdateClick(position);
                            return true;
                        case 3:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onShowClick(int position);

        void onUpdateClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

}
