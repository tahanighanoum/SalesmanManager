package com.dr7.salesmanmanager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dr7.salesmanmanager.Modles.Item;
import com.dr7.salesmanmanager.Modles.Offers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.viewHolder> {

    private List<Item> items;
    private ArrayList<Integer> isClicked = new ArrayList<>();
    private List<Item> filterList;
    private Context context;
    boolean added = false;
    DatabaseHandler MHandler;

    public RecyclerViewAdapter(List<Item> items, Context context) {
        this.items = items;
        this.filterList = items;
        this.context = context;
        for (int i = 0; i <= items.size(); i++) {
            isClicked.add(0);
        }
        MHandler = new DatabaseHandler(context);
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_listview, parent, false);

        return new viewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {

        holder.setIsRecyclable(false);
        if (isClicked.get(position) == 0)
            holder.linearLayout.setBackgroundColor(Color.parseColor("#455A64"));
        else
            holder.linearLayout.setBackgroundColor(R.color.done_button);

        holder.itemNumber.setText(items.get(holder.getAdapterPosition()).getItemNo());
        holder.itemName.setText(items.get(holder.getAdapterPosition()).getItemName());
        holder.tradeMark.setText(items.get(holder.getAdapterPosition()).getItemName());
        holder.category.setText("" + items.get(holder.getAdapterPosition()).getCategory());
        holder.unitQty.setText("" + items.get(holder.getAdapterPosition()).getQty());
        holder.price.setText("" + items.get(holder.getAdapterPosition()).getPrice());
        holder.tax.setText("" + items.get(holder.getAdapterPosition()).getTaxPercent());
        holder.barcode.setText(items.get(holder.getAdapterPosition()).getBarcode());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.add_item_dialog_small);

                final TextView itemNumber = (TextView) dialog.findViewById(R.id.item_number);
                final TextView itemName = (TextView) dialog.findViewById(R.id.item_name);
                final EditText price = (EditText) dialog.findViewById(R.id.price);
                final Spinner unit = (Spinner) dialog.findViewById(R.id.unit);
                final TextView textQty = (TextView) dialog.findViewById(R.id.textQty);
                final EditText unitQty = (EditText) dialog.findViewById(R.id.unitQty);
                final EditText unitWeight = (EditText) dialog.findViewById(R.id.unitWeight);
                final CheckBox useWeight = (CheckBox) dialog.findViewById(R.id.use_weight);
                final EditText bonus = (EditText) dialog.findViewById(R.id.bonus);
                final EditText discount = (EditText) dialog.findViewById(R.id.discount);
                final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.discTypeRadioGroup);
                final LinearLayout discountLinearLayout = (LinearLayout) dialog.findViewById(R.id.discount_linear);
                final LinearLayout unitWeightLinearLayout = (LinearLayout) dialog.findViewById(R.id.linearWeight);
                Button addToList = (Button) dialog.findViewById(R.id.addToList);

                itemNumber.setText(items.get(holder.getAdapterPosition()).getItemNo());
                itemName.setText(items.get(holder.getAdapterPosition()).getItemName());
                price.setText("" + items.get(holder.getAdapterPosition()).getPrice());

                final DatabaseHandler mHandler = new DatabaseHandler(context);

                if (mHandler.getAllSettings().get(0).getTaxClarcKind() == 1)
                    discountLinearLayout.setVisibility(View.INVISIBLE);

                if (mHandler.getAllSettings().get(0).getUseWeightCase() == 0) {
                    unitWeightLinearLayout.setVisibility(View.INVISIBLE);
                    textQty.setText(view.getContext().getResources().getString(R.string.app_qty));
                    useWeight.setChecked(false);
                } else
                    unitQty.setText("" + items.get(holder.getAdapterPosition()).getItemL());

                List<String> units = mHandler.getAllexistingUnits(itemNumber.getText().toString());

                ArrayAdapter<String> unitsList = new ArrayAdapter<String>(context, R.layout.spinner_style, units);
                unit.setAdapter(unitsList);

                addToList.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {

                        if (!price.getText().toString().equals("") && !price.getText().toString().equals("0")) {

                            Boolean check = check_Discount(unitWeight, unitQty, price, bonus, discount, radioGroup);
                            if (!check)
                                Toast.makeText(view.getContext(), "Invalid Discount Value please Enter a valid Discount", Toast.LENGTH_LONG).show();
                            else {

                                String unitValue;
                                if (mHandler.getAllSettings().get(0).getUseWeightCase() == 0) {
                                    unitValue = unit.getSelectedItem().toString();

                                    if (items.get(holder.getAdapterPosition()).getQty() >= Double.parseDouble(unitQty.getText().toString())
                                            || mHandler.getAllSettings().get(0).getAllowMinus() == 1
                                            || SalesInvoice.voucherType == 506) {

                                        if (mHandler.getAllSettings().get(0).getMinSalePric() == 0 || (mHandler.getAllSettings().get(0).getMinSalePric() == 1 &&
                                                Double.parseDouble(price.getText().toString()) >= items.get(holder.getAdapterPosition()).getMinSalePrice())) {


                                            AddItemsFragment2 obj = new AddItemsFragment2();
                                            Offers offer = checkOffers(itemNumber.getText().toString());

                                            if (offer != null) {
                                                if (offer.getPromotionType() == 0) {

                                                    added = obj.addItem(itemNumber.getText().toString(), itemName.getText().toString(),
                                                            holder.tax.getText().toString(), unitValue, unitQty.getText().toString(), price.getText().toString(),
                                                            bonus.getText().toString(), discount.getText().toString(), radioGroup, useWeight, view.getContext());

                                                    added = obj.addItem(offer.getBonusItemNo(), offer.getBonusItemNo() + "(bonus)",
                                                            "0", "1", "" + offer.getBonusQty(), "1",
                                                            "0", "0", radioGroup, useWeight, view.getContext());

                                                } else {
                                                    String priceAfterDiscount = ""+ (Double.parseDouble(price.getText().toString())- (Double.parseDouble(price.getText().toString())*offer.getBonusQty()));
                                                    added = obj.addItem(itemNumber.getText().toString(), itemName.getText().toString(),
                                                            holder.tax.getText().toString(), unitValue, unitQty.getText().toString(), priceAfterDiscount,
                                                            bonus.getText().toString(), discount.getText().toString(), radioGroup, useWeight, view.getContext());
                                                }
                                            } else {
                                                added = obj.addItem(itemNumber.getText().toString(), itemName.getText().toString(),
                                                        holder.tax.getText().toString(), unitValue, unitQty.getText().toString(), price.getText().toString(),
                                                        bonus.getText().toString(), discount.getText().toString(), radioGroup, useWeight, view.getContext());
                                            }
                                            if (added) {
                                                if (offer != null)
                                                    openOfferDialog(offer);

                                                holder.linearLayout.setBackgroundColor(R.color.done_button);
                                                isClicked.set(holder.getAdapterPosition(), 1);
                                            }
                                        } else
                                            Toast.makeText(view.getContext(), "Item hasn't been added, Min sale price for this item is " + items.get(holder.getAdapterPosition()).getMinSalePrice(), Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(view.getContext(), "Insufficient Quantity", Toast.LENGTH_LONG).show();
                                } else {
                                    if (unitWeight.getText().toString() == "")
                                        Toast.makeText(view.getContext(), "please enter unit weight", Toast.LENGTH_LONG).show();
                                    else {
                                        unitValue = unitWeight.getText().toString();
//                                        String qtyValue = "" + (Double.parseDouble(unitWeight.getText().toString()) * Double.parseDouble(unitQty.getText().toString()));
                                        String qty = (useWeight.isChecked() ? "" + (Double.parseDouble(unitQty.getText().toString()) * Double.parseDouble(unitValue)) : unitQty.getText().toString());

                                        Log.e("here**", "" + holder.getAdapterPosition());
                                        if (holder.getAdapterPosition() > -1) {
                                            if (items.get(holder.getAdapterPosition()).getQty() >= Double.parseDouble(qty)
                                                    || mHandler.getAllSettings().get(0).getAllowMinus() == 1
                                                    || SalesInvoice.voucherType == 506) {
                                                if (mHandler.getAllSettings().get(0).getMinSalePric() == 0 || (mHandler.getAllSettings().get(0).getMinSalePric() == 1 &&
                                                        Double.parseDouble(price.getText().toString()) >= items.get(holder.getAdapterPosition()).getMinSalePrice())) {

                                                    AddItemsFragment2 obj = new AddItemsFragment2();
                                                    Offers offer = checkOffers(itemNumber.getText().toString());

                                                    if (offer != null) {
                                                        if (offer.getPromotionType() == 0) {

                                                            added = obj.addItem(itemNumber.getText().toString(), itemName.getText().toString(),
                                                                    holder.tax.getText().toString(), unitValue, qty, price.getText().toString(),
                                                                    bonus.getText().toString(), discount.getText().toString(), radioGroup, useWeight, view.getContext());

                                                            added = obj.addItem(offer.getBonusItemNo(), "(Promotion)",
                                                                    "0", "1", "" + offer.getBonusQty(), "0",
                                                                    "0", "0", radioGroup, useWeight, view.getContext());

                                                        } else {
                                                            String priceAfterDiscount = ""+ (Double.parseDouble(price.getText().toString())- (Double.parseDouble(price.getText().toString())*offer.getBonusQty()));
                                                            added = obj.addItem(itemNumber.getText().toString(), itemName.getText().toString(),
                                                                    holder.tax.getText().toString(), unitValue, qty, priceAfterDiscount,
                                                                    bonus.getText().toString(), discount.getText().toString(), radioGroup, useWeight, view.getContext());
                                                        }
                                                    } else {
                                                        added = obj.addItem(itemNumber.getText().toString(), itemName.getText().toString(),
                                                                holder.tax.getText().toString(), unitValue, qty, price.getText().toString(),
                                                                bonus.getText().toString(), discount.getText().toString(), radioGroup, useWeight, view.getContext());
                                                    }
                                                    if (added) {
                                                        if (offer != null)
                                                            openOfferDialog(offer);
                                                        holder.linearLayout.setBackgroundColor(R.color.done_button);
                                                        isClicked.set(holder.getAdapterPosition(), 1);
                                                    }
                                                } else
                                                    Toast.makeText(view.getContext(), "Item hasn't been added, Min sale price for this item is " + items.get(holder.getAdapterPosition()).getMinSalePrice(), Toast.LENGTH_LONG).show();
                                            } else
                                                Toast.makeText(view.getContext(), "Insufficient Quantity", Toast.LENGTH_LONG).show();
                                        } else
                                            Toast.makeText(view.getContext(), "Please enter the item again", Toast.LENGTH_LONG).show();
                                    }
                                }

                            }

                            dialog.dismiss();
                        } else
                            Toast.makeText(view.getContext(), "Invalid price", Toast.LENGTH_LONG).show();


                    }
                });
                dialog.show();

            }
        });

    }

    private Offers checkOffers(String itemNo) {

        Offers offer = null;
        try {
            Date currentTimeAndDate = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String date = df.format(currentTimeAndDate);
            date = convertToEnglish(date);

            List<Offers> offers = MHandler.getAllOffers();
            for (int i = 0; i < offers.size(); i++) {
                if (itemNo.equals(offers.get(i).getItemNo()) &&
                        (formatDate(date).after(formatDate(offers.get(i).getFromDate())) || formatDate(date).equals(formatDate(offers.get(i).getFromDate()))) &&
                        (formatDate(date).before(formatDate(offers.get(i).getToDate())) || formatDate(date).equals(offers.get(i).getToDate()))) {

                    offer = offers.get(i);
                    break;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return offer;
    }

    public Date formatDate(String date) throws ParseException {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date d = sdf.parse(date);
        return d;
    }

    void openOfferDialog(Offers offers) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.offer_dialog);

        final TextView offerType = (TextView) dialog.findViewById(R.id.offerType);
        final TextView bonusItem = (TextView) dialog.findViewById(R.id.bonusItem);
        final TextView discount_value = (TextView) dialog.findViewById(R.id.discount_value);
        final TextView from = (TextView) dialog.findViewById(R.id.from);
        final TextView to = (TextView) dialog.findViewById(R.id.to);
        Button ok = (Button) dialog.findViewById(R.id.ok);

        String offType = offers.getPromotionType() == 0 ? context.getResources().getString(R.string.app_bonus) : context.getResources().getString(R.string.app_disc_);
        offerType.setText(offerType.getText().toString() + "  :       " + offType);

        String bonusItm = offers.getBonusItemNo().equals("-1") ? "none" : offers.getBonusQty() + " " + context.getResources().getString(R.string.of) + " " + offers.getBonusItemNo();
        bonusItem.setText(bonusItem.getText().toString() + " :     " + bonusItm);

        String disc = offers.getPromotionType() == 0 ? "0" : "" + offers.getBonusQty();
        discount_value.setText(discount_value.getText().toString() + " : " + disc);

        from.setText(from.getText().toString() + " : " + offers.getFromDate());
        to.setText(to.getText().toString() + " : " + offers.getToDate());

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        CardView cardView;
        TextView itemNumber, itemName, tradeMark, category, unitQty, price, tax, barcode;

        public viewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            linearLayout = itemView.findViewById(R.id.linear);
            itemNumber = itemView.findViewById(R.id.textViewItemNumber);
            itemName = itemView.findViewById(R.id.textViewItemName);
            tradeMark = itemView.findViewById(R.id.textViewTradeMark);
            category = itemView.findViewById(R.id.textViewCategory);
            unitQty = itemView.findViewById(R.id.textViewUnit_qty);
            price = itemView.findViewById(R.id.textViewPrice);
            tax = itemView.findViewById(R.id.textViewTax);
            barcode = itemView.findViewById(R.id.textViewBarcode);
        }
    }

    private Boolean check_Discount(EditText unitEditText, EditText qtyEditText, TextView priceEditText,
                                   EditText bonusEditText, EditText discEditText, RadioGroup discTypeRadioGroup) {
        Boolean check = true;

        if (unitEditText.getText().toString().equals(""))
            unitEditText.setText("0");
        else
            unitEditText.setText(convertToEnglish(unitEditText.getText().toString()));

        if (qtyEditText.getText().toString().equals(""))
            qtyEditText.setText("0");
        else
            qtyEditText.setText(convertToEnglish(qtyEditText.getText().toString()));

        if (priceEditText.getText().toString().equals(""))
            priceEditText.setText("0");
        else
            priceEditText.setText(convertToEnglish(priceEditText.getText().toString()));

        if (discEditText.getText().toString().equals(""))
            discEditText.setText("0");
        else
            discEditText.setText(convertToEnglish(discEditText.getText().toString()));

        if (bonusEditText.getText().toString().equals(""))
            bonusEditText.setText("0");
        else
            bonusEditText.setText(convertToEnglish(bonusEditText.getText().toString()));

        Float totalValue = (Float.parseFloat(qtyEditText.getText().toString())) * (Float.parseFloat(priceEditText.getText().toString()));
        Float discount = Float.parseFloat(discEditText.getText().toString());
        int radioId = discTypeRadioGroup.getCheckedRadioButtonId();

        if (radioId == R.id.discValueRadioButton) {
            if (discount > totalValue)
                return false;
        } else {
            if (discount > 100)
                return false;
        }

        return check;
    }

    public String convertToEnglish(String value) {
        String newValue = (((((((((((value + "").replaceAll("١", "1")).replaceAll("٢", "2")).replaceAll("٣", "3")).replaceAll("٤", "4")).replaceAll("٥", "5")).replaceAll("٦", "6")).replaceAll("٧", "7")).replaceAll("٨", "8")).replaceAll("٩", "9")).replaceAll("٠", "0"));
        return newValue;
    }
}
