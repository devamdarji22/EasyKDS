package com.jadd.easykds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference,reference;
    ArrayList<String> tableNumberList = new ArrayList<>();
    int tableNumber;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Table, TableNumberViewHolder> tableNumberAdapter;
    FirebaseRecyclerAdapter<Cart,OrderViewHolder> orderAdapter;
    StaggeredGridLayoutManager manager;
    //Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.staggered_recycler_view);
        manager = new StaggeredGridLayoutManager(3,LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        databaseReference = FirebaseDatabase.getInstance().getReference("Table");
        reference = FirebaseDatabase.getInstance().getReference("Table");

        Query query = FirebaseDatabase.getInstance().getReference("Table").orderByChild("sendToKitchen").equalTo(true);
        FirebaseRecyclerOptions<Table> options = new FirebaseRecyclerOptions.Builder<Table>()
                .setQuery(query,Table.class)
                .build();

        tableNumberAdapter = new FirebaseRecyclerAdapter<Table, TableNumberViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TableNumberViewHolder holder1, int position, @NonNull Table model) {
                holder1.tableNumberView.setText("Table Number - "+model.getTableNumber());
                FirebaseRecyclerOptions<Cart> options1 = new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(reference.child(Long.valueOf(model.getTableNumber()).toString()).child("Cart"),Cart.class)
                        .build();

                orderAdapter = new FirebaseRecyclerAdapter<Cart, OrderViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Cart model) {
                        holder.itemName.setText(model.getItem().getName());
                        holder.quantity.setText(String.valueOf(model.getQuantity()));
                    }

                    @NonNull
                    @Override
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.each_item,parent,false);
                        return new OrderViewHolder(view);
                    }
                };
                orderAdapter.startListening();
                orderAdapter.notifyDataSetChanged();
                holder1.orderView.setAdapter(orderAdapter);
            }

            @NonNull
            @Override
            public TableNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.each_order,parent,false);
                return new TableNumberViewHolder(view);
            }
        };
        tableNumberAdapter.startListening();
        tableNumberAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(tableNumberAdapter);






        /*databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tableNumberList.clear();
                if(dataSnapshot!=null) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if ((boolean) item.child("sendToKitchen").getValue()&&(boolean)item.child("empty").getValue()==false) {
                            //tableNumber = (int) item.child("tableNumber").getValue();
                            tableNumber = ((Long) item.child("tableNumber").getValue()).intValue();
                            //cart = item2.getValue(Cart.class);
                            tableNumberList.add(String.valueOf(tableNumber));

                        }
                    }

                    StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter
                            = new StaggeredRecyclerViewAdapter(MainActivity.this, tableNumberList);
                    StaggeredGridLayoutManager staggeredGridLayoutManager
                            = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    recyclerView.setAdapter(staggeredRecyclerViewAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }
}
