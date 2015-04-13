package com.echen.arthur.CustomControl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.echen.arthur.R;

import java.util.jar.Attributes;

/**
 * Created by echen on 2015/3/26.
 */
public class ClearableEditText extends LinearLayout {
    private EditText editText;
    private Button clearButton;

    //Simple constructor to use when creating a view from code
    public ClearableEditText(Context context)
    {
        super(context);
        init(context);
    }

    //Constructor that is called when inflating a view from XML
    public ClearableEditText(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init(context);
    }

    //Perform inflation from XML and apply a class-specific base style
    public ClearableEditText(Context context, AttributeSet attributeSet, int defStyle)
    {
        super(context, attributeSet, defStyle);
    }

    private void init(Context context)
    {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.customcontrol_clearable_edit_text, this, true);
        editText = (EditText)findViewById(R.id.editText);
        clearButton = (Button)findViewById(R.id.clearButton);
        hookupButton();
    }

    private void hookupButton()
    {
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
    }
}
