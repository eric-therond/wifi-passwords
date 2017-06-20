package com.wifisec;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BlankFragment2 extends Fragment {

    private interfaceFragment.OnFragmentInteractionListener mListener;

    public static BlankFragment2 newInstance() {
        BlankFragment2 fragment = new BlankFragment2();
        return fragment;
    }

    public BlankFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank_fragment2, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (interfaceFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        this.getDbPasswords();
        Button b1 = (Button) this.getActivity().findViewById(R.id.button);
        b1.setOnClickListener(saveconfig);
    }

    View.OnClickListener saveconfig = new View.OnClickListener() {
        public void onClick(View v) {

            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getActivity());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            db.execSQL("delete from "+ FeedReaderContract.FeedEntry.TABLE_NAME_PASSWORDS);

            EditText editText = (EditText)getView().findViewById(R.id.editText);
            String etext = editText.getText().toString();
            String[] passwords;
            String delimiter = "\n";

            passwords = etext.split(delimiter);
            for(int i =0; i < passwords.length; i++)
            {
                ContentValues values1 = new ContentValues();
                values1.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE, passwords[i]);
                long newRowId1 = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_PASSWORDS, "null", values1);
            }

            Toast.makeText(v.getContext(), Html.fromHtml("Configuration enregistrée"), Toast.LENGTH_SHORT).show();
        }
    };

    public void getDbPasswords() {

        EditText editText = (EditText)getView().findViewById(R.id.editText);

        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this.getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE
        };

        String sortOrder =
                FeedReaderContract.FeedEntry._ID + " DESC";

        Cursor c = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME_PASSWORDS,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while (c.moveToNext()) {
            long itemId = c.getLong(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            String title = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE));


            editText.setText(editText.getText() + title + "\n", TextView.BufferType.EDITABLE);
        }
    }
}
