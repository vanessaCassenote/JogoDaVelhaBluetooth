package com.example.vanessa.jogodavelhabluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TelaVencedor extends AppCompatActivity {

    TextView ganhador;
    String mensagemRecebida;
    Button novoJogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_vencedor);
        novoJogo = (Button) findViewById(R.id.btnNovoJogo);
        novoJogo.setOnClickListener(ButtonNovoJogo);

        Intent receberMensagem = getIntent();
        mensagemRecebida = receberMensagem.getStringExtra(TelaJogo.MENSAGEM);
        ganhador = (TextView) findViewById(R.id.ganhador);
        ganhador.setText(mensagemRecebida);
    }

    View.OnClickListener ButtonNovoJogo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), TelaNome.class);
            startActivity(i);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_vencedor, menu);
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
