package com.example.car_mad;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class bookAdapter extends RecyclerView.Adapter<bookAdapter.ProductViewHolder>  {
private  Context mCtx;
private  List<BookCarModel> bookList;


public bookAdapter(Context context, List<BookCarModel> bookLists) {
        mCtx = context;
        bookList = bookLists;
        }

@Override

public bookAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mCtx).inflate(R.layout.book_list_item,parent,false);
        return new ProductViewHolder(v);
        }

@Override
public void onBindViewHolder(bookAdapter.ProductViewHolder holder, int position) {
        BookCarModel book = bookList.get(position);


        holder.text_location.setText(book.getLocation());
        holder.text_date.setText(book.getDate());
        holder.text_time.setText(book.getTime());
        holder.text_day.setText(book.getDays());
        holder.text_total.setText(book.getTotal());

        }

@Override
public int getItemCount() {
        return bookList.size();
        }




public  class ProductViewHolder extends RecyclerView.ViewHolder{

    public CardView cardview;
    public TextView text_location,text_date,text_time,text_day,text_total,day;


    public ProductViewHolder(View itemView) {
        super(itemView);

        //set onClickListener for itemView
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),BookUpdate.class);
                intent.putExtra("location", bookList.get(getBindingAdapterPosition()).getLocation());
                intent.putExtra("date", bookList.get(getBindingAdapterPosition()).getDate());
                intent.putExtra("time", bookList.get(getBindingAdapterPosition()).getTime());
                intent.putExtra("day", bookList.get(getBindingAdapterPosition()).getDays());
                intent.putExtra("total", bookList.get(getBindingAdapterPosition()).getTotal());


                view.getContext().startActivity(intent);
            }
        });
        cardview = itemView.findViewById(R.id.bookCard);
        text_location = itemView.findViewById(R.id.location);
        text_date = itemView.findViewById(R.id.date);
        text_time = itemView.findViewById(R.id.time);
        text_day = itemView.findViewById(R.id.days);
        text_total = itemView.findViewById(R.id.totalAmount);
        day = itemView.findViewById(R.id.day);


    }


}

}
