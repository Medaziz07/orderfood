package com.android.OrderFood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.OrderFood.adapters.MenuListAdapter;
import com.android.OrderFood.model.Menu;
import com.android.OrderFood.model.RestaurantModel;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuActivity extends AppCompatActivity implements MenuListAdapter.MenuListClickListener {
    //declaration de list de menu initialement vide
    private List<Menu> menuList = null;
    //appel au menuadapter
    private MenuListAdapter menuListAdapter;
    //list des objets dans le panier
    private List<Menu> itemsInCartList;
    //compteur d'item dans le panier initialement 0
    private int totalItemInCart = 0;
    private TextView buttonCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);


        menuList = restaurantModel.getMenus();
        initRecyclerView();


         buttonCheckout = findViewById(R.id.buttonCheckout);
         //naviguer avec l'ordre reservé vers la page de paiement(placeyourorder)
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemsInCartList != null && itemsInCartList.size() <= 0) {
                    //si no item selectionné et ve passer vers next page un petit message s'afficher
                    Toast.makeText(RestaurantMenuActivity.this, "Please add some items in cart.", Toast.LENGTH_SHORT).show();
                    return;
                }
                restaurantModel.setMenus(itemsInCartList);
                Intent i = new Intent(RestaurantMenuActivity.this, PlaceYourOrderActivity.class);
                i.putExtra("RestaurantModel", restaurantModel);
                startActivityForResult(i, 1000);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView =  findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        menuListAdapter = new MenuListAdapter(menuList, this);
        recyclerView.setAdapter(menuListAdapter);
    }
//l'ajout des items dans le panier
    @Override
    public void onAddToCartClick(Menu menu) {
        //si le panier est vide creer une list vide
        if(itemsInCartList == null) {
            itemsInCartList = new ArrayList<>();
        }

        itemsInCartList.add(menu);
        totalItemInCart = 0;
//compteur pour les items ajoutés
        for(Menu m : itemsInCartList) {
            totalItemInCart = totalItemInCart + m.getTotalInCart();
        }
        //affichage du compteur
        buttonCheckout.setText("Checkout (" +totalItemInCart +") items");
    }
//modifier l'ordre (ajouter ou supprimer)
    @Override
    public void onUpdateCartClick(Menu menu) {
        if(itemsInCartList.contains(menu)) {
            int index = itemsInCartList.indexOf(menu);
            itemsInCartList.remove(index);
            itemsInCartList.add(index, menu);

            totalItemInCart = 0;

            for(Menu m : itemsInCartList) {
                totalItemInCart = totalItemInCart + m.getTotalInCart();
            }
            buttonCheckout.setText("Checkout (" +totalItemInCart +") items");
        }
    }

    @Override
    public void onRemoveFromCartClick(Menu menu) {
        if(itemsInCartList.contains(menu)) {
            itemsInCartList.remove(menu);
            totalItemInCart = 0;

            for(Menu m : itemsInCartList) {
                totalItemInCart = totalItemInCart + m.getTotalInCart();
            }
            buttonCheckout.setText("Checkout (" +totalItemInCart +") items");
        }
    }
//cette method est appelé chaque fois que l'item est appelé
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }
//lorsqu'une acitivé se termine onActivityresult retourne le restultat
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            //
            finish();
        }
    }
}