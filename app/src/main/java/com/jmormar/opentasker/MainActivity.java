package com.jmormar.opentasker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jmormar.opentasker.entities.Agenda;
import com.jmormar.opentasker.util.DBHelper;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView label = findViewById(R.id.displayLabel);

        DBHelper helper = DBHelper.getInstance(this);

        Agenda pp = new Agenda();
        pp.setNombre("Test");
        pp.setFechaInicio(new Date(454545465));
        pp.setFechaFinal( new Date(460000000));

        if(helper.insertarAgenda(pp)){
            label.setText("Todo ha ido bien");
            return;
        }
        label.setText("Jey ror");

    }
}