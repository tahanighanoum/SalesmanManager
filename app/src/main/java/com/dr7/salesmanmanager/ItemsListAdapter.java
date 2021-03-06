package com.dr7.salesmanmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dr7.salesmanmanager.Modles.Item;

import java.util.List;

/**
 * Created by mohd darras on 29/12/2017.
 */

public class ItemsListAdapter extends BaseAdapter {

    private Context context;
    private List<Item> itemList;

    public ItemsListAdapter(Context context, List<Item> itemList)
    {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemsList(List<Item> itemList)
    {
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View myView = View.inflate(context, R.layout.items_list,null);
        TextView itemNoTextView = (TextView) myView.findViewById(R.id.itemNoTextView);
        TextView itemNameTextView = (TextView) myView.findViewById(R.id.DescTextView);
        TextView qtyTextView = (TextView) myView.findViewById(R.id.qtyTextView);
        TextView priceTextView = (TextView) myView.findViewById(R.id.priceTextView);
        TextView bonusTextView = (TextView) myView.findViewById(R.id.bonusTextView);
        TextView lineDescTextView = (TextView) myView.findViewById(R.id.lineDiscTextView);
        TextView amountTextView = (TextView) myView.findViewById(R.id.amountTextView);

        itemNoTextView.setText(itemList.get(i).getItemNo());
        itemNameTextView.setText(itemList.get(i).getItemName());
        qtyTextView.setText(""+itemList.get(i).getQty());
        priceTextView.setText(String.valueOf(itemList.get(i).getPrice()));
        bonusTextView.setText(String.valueOf(itemList.get(i).getBonus()));
        lineDescTextView.setText(String.valueOf(itemList.get(i).getDisc()));
        amountTextView.setText(String.valueOf(itemList.get(i).getAmount()));

        return myView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
