package com.example.car_mad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RentAdapter extends RecyclerView.Adapter<RentAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<RentCarModel> rentList;

    public RentAdapter(Context context, List<RentCarModel> rentLists) {
        mCtx = context;
        rentList = rentLists;
    }

    @Override

    public RentAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mCtx).inflate(R.layout.rent_list_item,parent,false);
        return new ProductViewHolder(v);
    }
    @Override
    public void onBindViewHolder(RentAdapter.ProductViewHolder holder, int position) {
        RentCarModel rent = rentList.get(position);


        holder.text_brand.setText(rent.getBrand());
        holder.text_model.setText(rent.getModel());
        holder.text_regNo.setText(rent.getRegisNo());
        holder.text_description.setText(rent.getDescription());
        Picasso
                .get()
                .load(rent.getImageURL())
                .placeholder(R.drawable.placceholder)
                .fit()
                .into(holder.imageView_Product);


    }

    @Override
    public int getItemCount() {
        return rentList.size();
    }

    public  class ProductViewHolder extends RecyclerView.ViewHolder{

        public CardView cardview;
        public TextView text_brand,text_model,text_regNo,text_description;
        public ImageView imageView_Product;

        public ProductViewHolder(View itemView) {
            super(itemView);

            //set onClickListener for itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),RentCarUpdate.class);
                    intent.putExtra("brand", rentList.get(getBindingAdapterPosition()).getBrand());
                    intent.putExtra("model", rentList.get(getBindingAdapterPosition()).getModel());
                    intent.putExtra("regNo", rentList.get(getBindingAdapterPosition()).getRegisNo());
                    intent.putExtra("Description", rentList.get(getBindingAdapterPosition()).getDescription());
                    intent.putExtra("image_url", rentList.get(getBindingAdapterPosition()).getImageURL());


                    view.getContext().startActivity(intent);
                }
            });
            cardview = itemView.findViewById(R.id.rentCard);
            text_brand = itemView.findViewById(R.id.rentBrand);
            text_model = itemView.findViewById(R.id.rentModel);
            text_regNo = itemView.findViewById(R.id.rentRegNo);
            text_description = itemView.findViewById(R.id.description);
            imageView_Product = itemView.findViewById(R.id.adminImage);



        }





    }


}
