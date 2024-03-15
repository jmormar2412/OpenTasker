package com.jmormar.opentasker;

import static org.mockito.Mockito.mock;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jmormar.opentasker.entities.Agenda;
import com.jmormar.opentasker.util.DBHelper;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest extends AppCompatActivity{
    private DBHelper helper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        this.helper = mock(DBHelper.class);
    }

    @Test
    public void insertionTest(){
        Agenda g = new Agenda();
        g.setNombre("njdf");
        g.setFechaInicio(new Date(12345678));
        g.setFechaFinal(new Date(12555555));

        helper.insertarAgenda(g);

        Agenda rec = helper.getAgenda();

        Assert.assertEquals(g.getNombre(), rec.getNombre());

    }

}