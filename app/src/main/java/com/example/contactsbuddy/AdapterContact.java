package com.example.contactsbuddy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ContactViewHolder> {

    private Context context;
    private ArrayList<ModelContact> contactList;
    private DbHelper dbHelper;

    public AdapterContact(Context context, ArrayList<ModelContact> contactList) {
        this.context = context;
        this.contactList = contactList;
        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public AdapterContact.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contact_item,parent,false);
        ContactViewHolder vh = new ContactViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterContact.ContactViewHolder holder, int position) {
        
        ModelContact modelContact = contactList.get(position);

        String id = modelContact.getId();
//        String image = modelContact.getImage();
        String name = modelContact.getName();
        String phone = modelContact.getPhone();
        String email = modelContact.getEmail();
        String note = modelContact.getNote();
        String addedTime = modelContact.getAddedTime();
        String updatedTime = modelContact.getUpdatedTime();

        // set data in view
        holder.contactName.setText(name);

//        if (image.equals("")){
//            holder.contactImage.setImageResource(R.drawable.baseline_person_24);
//        }
//        else {
//            holder.contactImage.setImageURI(Uri.parse(image));
//        }

        holder.contactDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // handle item click and show contact details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent to move contact details activity with contact id as reference
                Intent intent = new Intent(context,ContactDetails.class);
                intent.putExtra("contactID",id);
                context.startActivity(intent);
            }
        });

        // handle Edit button
        holder.contactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent to move AddEditActivity to update date
                Intent intent = new Intent(context, AddEditContact.class);
                // pass the value of current position
                intent.putExtra("ID",id);
                intent.putExtra("NAME",name);
                intent.putExtra("PHONE",phone);
                intent.putExtra("EMAIL",email);
                intent.putExtra("NOTE",note);
                intent.putExtra("ADDEDTIME",addedTime);
                intent.putExtra("UPDATEDTIME",updatedTime);
//                intent.putExtra("IMAGE",image);

                // pass boolean data to define it is for edit purpose
                intent.putExtra("isEditMode", true);

                context.startActivity(intent);
            }
        });

        // handle Delete button
        holder.contactDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteContact(id);
                Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();

                // refresh data
                ((MainActivity)context).onResume();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{

        // view for row_contact_item
        ImageView contactImage,contactDial;
        TextView contactName,contactEdit,contactDelete;
        RelativeLayout relativeLayout;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            // init view
            contactImage = itemView.findViewById(R.id.contact_image);
            contactDial = itemView.findViewById(R.id.contact_number_dial);
            contactName = itemView.findViewById(R.id.contact_name);
            contactEdit = itemView.findViewById(R.id.contact_edit);
            contactDelete = itemView.findViewById(R.id.contact_delete);
            relativeLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
