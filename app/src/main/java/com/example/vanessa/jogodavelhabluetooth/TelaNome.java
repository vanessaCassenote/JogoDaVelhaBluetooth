package com.example.vanessa.jogodavelhabluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TelaNome extends AppCompatActivity {
    private Button btnPlay;
    private EditText nomeJogador;
    private String[] nomeJogadores = new String[2];
    public final static String MENSAGEM = "com.example.vanessa.activities.MENSAGEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_nome);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        nomeJogador = (EditText) findViewById(R.id.fieldNome);
        btnPlay.setOnClickListener(buttonPlay);
    }

    View.OnClickListener buttonPlay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(nomeJogador.getText() != null){
                Intent i = new Intent(getApplicationContext(), TelaJogo.class);
                i.putExtra(MENSAGEM,String.valueOf(nomeJogador.getText()));
                startActivity(i);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_nome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
