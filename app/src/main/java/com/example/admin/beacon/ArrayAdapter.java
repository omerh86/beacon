package com.example.admin.beacon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Admin on 1/28/2016.
 */
public class ArrayAdapter extends android.widget.ArrayAdapter {

    ArrayAdapter adapter;
    ArrayList<Person> person;
    Context context;
    int textViewResource;
    int resource;
    int positionOfRowView = 0;

    @SuppressWarnings("unchecked")
    public ArrayAdapter(Context context, int resource, int textViewResourceId,
                        ArrayList<Person> person) {
        super(context, resource, textViewResourceId, person);

        this.adapter = this;
        this.resource = resource;
        this.textViewResource = textViewResourceId;
        this.context = context;
        this.person = person;
        // this.imageIds = imageIds;
    }


    static class ViewContainer {

        public TextView txtTitle;
        public ImageView img;
        public Button delete;
        //public LinearLayout wrapper;
    }

    //
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Data data = Data.getinstance();

        ViewContainer viewContainer;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(resource, null);
            viewContainer = new ViewContainer();
            viewContainer.txtTitle = (TextView) rowView
                    .findViewById(this.textViewResource);

            viewContainer.img = (ImageView) rowView
                    .findViewById(R.id.contacticon);

            //viewContainer.delete= (Button) rowView.findViewById(R.id.delete);
            final View finalRowView = rowView;
            // viewContainer.delete.setOnClickListener(new View.OnClickListener() {
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(context, finalRowView);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.Add:
                                    data.numberOfArrayList = position;
                                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                    ((Activity) context).startActivityForResult(intent, Data.CONTACT_PICKER_RESULT);
                                    return true;
                                case R.id.Remove:

                                    data.numberOfArrayList = position;
                                    person.set(position, new Person());
                                    data.iconsArray[position] = null;
                                    adapter.notifyDataSetChanged();
                                    Log.d("person", "" + person.get(position).getName());
                                    return true;
//                                case R.id.Changemassege:
//                                    data.numberOfArrayList = position;
//                                    Settings.massageInput.setVisibility(View.VISIBLE);
//                                    Settings.massageInput.setText(person.get(position).getMassege());
//                                    Log.d("massege", "" + person.get(position).getMassege());
//                                    Log.d("massege2", "" + data.contactArray.get(position).getMassege());
//                                    return true;
                            }
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.menu_popup);
                    Menu m = popupMenu.getMenu();

                    if (person.get(position).getName().equals("")) {
                        //m.removeItem(R.id.Remove);
                        MenuItem mi= m.getItem(1);
                        mi.setEnabled(false);

                    } else {
                      //  m.add(R.id.Remove);
                        MenuItem mi= m.getItem(1);
                        mi.setEnabled(true);
                    }
                    popupMenu.show();

                }
            });

            rowView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) rowView.getTag();
        }

        viewContainer.txtTitle.setText((person.get(position)).getName());
        // if ((person.get(position)).getIcon() != null) {
        if (data.iconsArray[position] != null) {
            Bitmap bitmap = data.iconsArray[position];
            bitmap = Data.getRoundedShape(bitmap);
            viewContainer.img.setImageBitmap(bitmap);

        } else {
            viewContainer.img.setImageResource(R.drawable.user2);
        }
        positionOfRowView = position;
        return rowView;
    }


}