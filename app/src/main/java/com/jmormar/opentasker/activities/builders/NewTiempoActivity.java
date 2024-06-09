package com.jmormar.opentasker.activities.builders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Tiempo;
import com.jmormar.opentasker.util.DBHelper;

public class NewTiempoActivity extends AppCompatActivity {

    private NumberPicker pickerMinutos, pickerSegundos;
    private int idPomodoro;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_tiempo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.helper = DBHelper.getInstance(this);

        this.idPomodoro = getIntent().getIntExtra("idPomodoro", -1);
        if(getIntent().getBooleanExtra("activateRest", false)){
            SwitchMaterial switchDescanso = findViewById(R.id.sw_descanso);
            switchDescanso.setChecked(true);
        }

        Button aceptar = findViewById(R.id.bt_newtiempo_aceptar);
        aceptar.setOnClickListener(v -> aceptar());

        this.pickerMinutos = findViewById(R.id.selector_minutos);
        this.pickerSegundos = findViewById(R.id.selector_segundos);

        pickerMinutos.setMinValue(0);
        pickerMinutos.setMaxValue(99);
        pickerSegundos.setMinValue(0);
        pickerSegundos.setMaxValue(59);

    }

    private void aceptar(){
        SwitchMaterial switchDescanso = findViewById(R.id.sw_descanso);
        boolean descanso = switchDescanso.isChecked();

        if(pickerMinutos.getValue() == 0 && pickerSegundos.getValue() == 0){
            Toast.makeText(this, getString(R.string.selecciona_algo_tiempo), Toast.LENGTH_SHORT).show();
            return;
        }

        Tiempo tiempo = new Tiempo();
        tiempo.setSetSeconds(pickerMinutos.getValue() * 60 + pickerSegundos.getValue());
        tiempo.setUpdatedSeconds(tiempo.getSetSeconds());
        tiempo.setRest(descanso);
        tiempo.setIdPomodoro(this.idPomodoro);

        if(this.helper.insertarTiempo(tiempo)){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("idPomodoro", this.idPomodoro);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else{
            Toast.makeText(this, getString(R.string.error_insertando) + getString(R.string.tiempo), Toast.LENGTH_SHORT).show();
        }
    }
}