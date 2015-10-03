package com.example.vanessa.jogodavelhabluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TelaJogo extends AppCompatActivity {

    Button btna;
    Button btnb;
    Button btnc;
    Button btnd;
    Button btne;
    Button btnf;
    Button btng;
    Button btnh;
    Button btni;
    TextView jogando;
    private int jogador = 1;
    private char[][] matrizJogo = new char[3][3];
    Drawable buttonBackground;
    String[] mensagemRecebida = new String[2];
    String nomeJogador;
    public final static String MENSAGEM = "com.example.vanessa.activities.MENSAGEM";

    // Códigos para troca de intent
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int RESTART = 3;

    // Itens de layout
    Button sendButton;
    TextView msgTextView;

    private BluetoothAdapter myBluetoothAdapter;
    // Nome do dispositivo conectado
    public static String mConnectedDeviceName = null;

    // Retornos do handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Adaptador Bluetooth
    public static BluetoothService mBtService = null;

    // Tipos de mensagem enviadas pelo Handler do BluetoothService
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    //Guarda o nome do outro jogador para ser mostrado
    //na tela e enviar para a TelaVencedor caso ele ganhe.
    private String nomeRecebido;

    //flagGanhador indica se foi o jogador local, o outro
    //jogador ou se nenhum dos dois venceu.
    //matrizCheia indica que a matriz ja foi preenchida
    //portanto ninguem venceu.
    private int flagGanhador, matrizCheia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_jogo);
        btna = (Button) findViewById(R.id.a);
        btnb = (Button) findViewById(R.id.b);
        btnc = (Button) findViewById(R.id.c);
        btnd = (Button) findViewById(R.id.d);
        btne = (Button) findViewById(R.id.e);
        btnf = (Button) findViewById(R.id.f);
        btng = (Button) findViewById(R.id.g);
        btnh = (Button) findViewById(R.id.h);
        btni = (Button) findViewById(R.id.i);
        btna.setOnClickListener(buttona);
        btnb.setOnClickListener(buttonb);
        btnc.setOnClickListener(buttonc);
        btnd.setOnClickListener(buttond);
        btne.setOnClickListener(buttone);
        btnf.setOnClickListener(buttonf);
        btng.setOnClickListener(buttong);
        btnh.setOnClickListener(buttonh);
        btni.setOnClickListener(buttoni);
        buttonBackground = btna.getBackground();
        jogando = (TextView) findViewById(R.id.Jogadores);
        limpaMatriz();
        flagGanhador = 0;
        matrizCheia = 0;

        //Recebe nome do jogador local
        Intent receberMensagem = getIntent();
        nomeJogador = receberMensagem.getStringExtra(TelaNome.MENSAGEM);

        // instancia o adaptador bluetooth do dispositivo
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
    public void desabilitaBotoes(){
        btna.setEnabled(false);
        btnb.setEnabled(false);
        btnc.setEnabled(false);
        btnd.setEnabled(false);
        btne.setEnabled(false);
        btnf.setEnabled(false);
        btng.setEnabled(false);
        btnh.setEnabled(false);
        btni.setEnabled(false);
    }
    public void habilitaBotoes(){
        btna.setEnabled(true);
        btnb.setEnabled(true);
        btnc.setEnabled(true);
        btnd.setEnabled(true);
        btne.setEnabled(true);
        btnf.setEnabled(true);
        btng.setEnabled(true);
        btnh.setEnabled(true);
        btni.setEnabled(true);
    }
    public void limpaMatriz(){
        for(int i = 0 ; i < 3; i++){
            for(int j = 0 ; j < 3; j++){
                matrizJogo[i][j] = ' ';
            }
        }
    }
    public void chamaTelaVencedor(){
        //Envia o nome o jogador que venceu para a tela do vencedor.
        //se flagGanhador == 0 o jogador local venceu senao foi o
        //outro jogador.
        Intent i = new Intent(getApplicationContext(), TelaVencedor.class);
        if(flagGanhador == 0){
            i.putExtra(MENSAGEM,String.valueOf(nomeJogador + " Ganhou!!!"));
        }else if(flagGanhador == 1){
            i.putExtra(MENSAGEM, String.valueOf(nomeRecebido +" Ganhou!!!"));
        }else if(flagGanhador == 2){
            i.putExtra(MENSAGEM,"Ninguem Ganhou =( ");
        }
        startActivity(i);
    }
    public void atualizaMatriz(String posicaoMatriz){
        char[] valorMatriz = posicaoMatriz.toCharArray();

        //Testa se o primeiro caracter e um digito.
        //Se nao for entao e o nome do jogador sendo recebido.
        if(!Character.isDigit(valorMatriz[0])){
            jogando.setText(nomeJogador+"  vs  "+String.valueOf(valorMatriz));
            nomeRecebido = String.valueOf(valorMatriz);
            return;
        }
        //As variaveis i e j guardam a posicao da matriz
        //que foi alterada pelo outro jogador e que precisa
        //ser atualizada localmente.
        //Em seguida a matriz e atualizada.
        int i, j;
        i = Integer.parseInt(String.valueOf(valorMatriz[0]));
        j = Integer.parseInt(String.valueOf(valorMatriz[1]));
        matrizJogo[i][j] = valorMatriz[2];

        //Seleciona qual botao deve ser atualizado de
        //acordo com a posicao da matriz.
        if((i == 0) && (j == 0)){
            btna.setText(String.valueOf(valorMatriz[2]));
        }else if((i == 0) && (j == 1)){
            btnb.setText(String.valueOf(valorMatriz[2]));
        }else if((i == 0) && (j == 2)){
            btnc.setText(String.valueOf(valorMatriz[2]));
        }else if((i == 1) && (j == 0)){
            btnd.setText(String.valueOf(valorMatriz[2]));
        }else if((i == 1) && (j == 1)){
            btne.setText(String.valueOf(valorMatriz[2]));
        }else if((i == 1) && (j == 2)){
            btnf.setText(String.valueOf(valorMatriz[2]));
        }else if((i == 2) && (j == 0)){
            btng.setText(String.valueOf(valorMatriz[2]));
        }else if((i == 2) && (j == 1)){
            btnh.setText(String.valueOf(valorMatriz[2]));
        }else if((i == 2) && (j == 2)){
            btni.setText(String.valueOf(valorMatriz[2]));
        }
        //Se o quarto bit recebido for 1 o outro jogador
        //venceu e o jogo deve ser encerrado.
        if(valorMatriz[3] == '1'){
            //se flagGanhador == 1 entao foi o outro jogador que venceu
            //caso contrario foi o jogador local.
            flagGanhador = 1;
            chamaTelaVencedor();
        }
        //Altera o jogador(1=X ou 2=O) no envio e na recepcao
        //de uma mensagem para que sempre o mesmo jogador
        //fique no mesmo device.
        //O jogador que comeca sera o X, ou seja, jogador = 1.
        if(jogador == 1){
            jogador = 2;
        }else{
            jogador = 1;
        }
        //Variavel que guarda o numero de jogadas(no envio e na recepcao)
        //se chegar ao numero de 10 jogadas(9 da matriz + 1 do envio do nome)
        //entao e pq a matriz esta cheia e ninguem ganhou.
        matrizCheia++;
        if(matrizCheia == 10){
            //Se flagGanhador == 2 ninguem venceu.
            flagGanhador = 2;
            chamaTelaVencedor();
        }
    }
    //Quando a conexao foi estabelecida os jogadores enviam
    //os seus nomes um para o outro, mostrando na tela ambos
    //os nomes.
    public void enviaNome(){
        sendMessage(String.valueOf(nomeJogador));
    }
    View.OnClickListener buttona = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(matrizJogo[0][0] == ' ') {
                if (jogador == 1) {
                    btna.setText("X");
                    matrizJogo[0][0] = 'X';
                } else if (jogador == 2) {
                    btna.setText("O");
                    matrizJogo[0][0] = 'O';
                }

                //Testa a primeira linha da matriz
                if ((matrizJogo[0][1] == matrizJogo[0][2]) && (matrizJogo[0][0] == matrizJogo[0][2])) {
                    //Muda a cor dos botoes onde ganhou o jogo
                    btna.setBackgroundColor(173216230);
                    btnb.setBackgroundColor(173216230);
                    btnc.setBackgroundColor(173216230);
                    //envia uma mensagem com a posicao da matriz + valor(X ou O) + bit que indica
                    //que o jogador venceu.
                    sendMessage(String.valueOf("00" + matrizJogo[0][0])+"1");
                    //Chama a tela onde mostra o nome no jogador que venceu.
                    chamaTelaVencedor();

                    //Testa a primeira coluna da matriz
                } else if ((matrizJogo[1][0] == matrizJogo[2][0]) && (matrizJogo[0][0] == matrizJogo[2][0])) {
                    btna.setBackgroundColor(173216230);
                    btnd.setBackgroundColor(173216230);
                    btng.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("00" + matrizJogo[0][0]) + "1");
                    chamaTelaVencedor();

                    //Testa a diagonal principal da matriz
                } else if ((matrizJogo[1][1] == matrizJogo[2][2]) && (matrizJogo[0][0] == matrizJogo[2][2])) {
                    btna.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btni.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("00" + matrizJogo[0][0]) + "1");
                    chamaTelaVencedor();
                }else{
                    //envia uma mensagem com a posicao da matriz + valor(X ou O) + bit que indica
                    //que o jogador nao venceu.
                    sendMessage(String.valueOf("00" + matrizJogo[0][0])+"0");
                }
            }
        }
    };
    View.OnClickListener buttonb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(matrizJogo[0][1] == ' ') {
                if (jogador == 1) {
                    btnb.setText("X");
                    matrizJogo[0][1] = 'X';
                } else if (jogador == 2) {
                    btnb.setText("O");
                    matrizJogo[0][1] = 'O';
                }
                //Testa a primeira linha da matriz
                if ((matrizJogo[0][1] == matrizJogo[0][2]) && (matrizJogo[0][0] == matrizJogo[0][2])) {
                    btna.setBackgroundColor(173216230);
                    btnb.setBackgroundColor(173216230);
                    btnc.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("01" + matrizJogo[0][1]+"1"));
                    chamaTelaVencedor();

                    //Testa a segunda coluna da matriz
                } else if ((matrizJogo[0][1] == matrizJogo[1][1]) && (matrizJogo[0][1] == matrizJogo[2][1])) {
                    btnb.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btnh.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("01" + matrizJogo[0][1] + "1"));
                    chamaTelaVencedor();
                }else{
                    sendMessage(String.valueOf("01" + matrizJogo[0][1]+"0"));
                }
            }
        }
    };
    View.OnClickListener buttonc = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(matrizJogo[0][2] == ' ') {
                if (jogador == 1) {
                    btnc.setText("X");
                    matrizJogo[0][2] = 'X';
                } else if (jogador == 2) {
                    btnc.setText("O");
                    matrizJogo[0][2] = 'O';
                }
                //Testa a primeira linha da matriz
                if ((matrizJogo[0][1] == matrizJogo[0][2]) && (matrizJogo[0][0] == matrizJogo[0][2])) {
                    btna.setBackgroundColor(173216230);
                    btnb.setBackgroundColor(173216230);
                    btnc.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("02" + matrizJogo[0][2]+"1"));
                    chamaTelaVencedor();

                    //Testa a terceira coluna da matriz
                } else if ((matrizJogo[0][2] == matrizJogo[1][2]) && (matrizJogo[0][2] == matrizJogo[2][2])) {
                    btnc.setBackgroundColor(173216230);
                    btnf.setBackgroundColor(173216230);
                    btni.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("02" + matrizJogo[0][2]+"1"));
                    chamaTelaVencedor();

                    //Testa a diagonal secundaria da matriz
                } else if ((matrizJogo[0][2] == matrizJogo[1][1]) && (matrizJogo[0][2] == matrizJogo[2][0])) {
                    btnc.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btng.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("02" + matrizJogo[0][2]+"1"));
                    chamaTelaVencedor();
                }else{
                    sendMessage(String.valueOf("02"+matrizJogo[0][2]+"0"));
                }
            }
        }
    };
    View.OnClickListener buttond = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(matrizJogo[1][0] == ' ') {
                if (jogador == 1) {
                    btnd.setText("X");
                    matrizJogo[1][0] = 'X';
                } else if (jogador == 2) {
                    btnd.setText("O");
                    matrizJogo[1][0] = 'O';
                }
                //Testa a primeira coluna da matriz
                if ((matrizJogo[1][0] == matrizJogo[0][0]) && (matrizJogo[1][0] == matrizJogo[2][0])) {
                    btna.setBackgroundColor(173216230);
                    btnd.setBackgroundColor(173216230);
                    btng.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("10" + matrizJogo[1][0]+"1"));
                    chamaTelaVencedor();

                    //Testa a segunda linha da matriz
                } else if ((matrizJogo[1][0] == matrizJogo[1][1]) && (matrizJogo[1][0] == matrizJogo[1][2])) {
                    btnd.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btnf.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("10" + matrizJogo[1][0] + "1"));
                    chamaTelaVencedor();
                }else{
                    sendMessage(String.valueOf("10" + matrizJogo[1][0]+"0"));
                }
            }
        }
    };
    View.OnClickListener buttone = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(matrizJogo[1][1] == ' ') {
                if (jogador == 1) {
                    btne.setText("X");
                    matrizJogo[1][1] = 'X';
                } else if (jogador == 2) {
                    btne.setText("O");
                    matrizJogo[1][1] = 'O';
                }
                //Testa a diagonal principal da matriz
                if ((matrizJogo[1][1] == matrizJogo[2][2]) && (matrizJogo[0][0] == matrizJogo[2][2])) {
                    btna.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btni.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("11" + matrizJogo[1][1]+"1"));
                    chamaTelaVencedor();

                    //Testa a diagonal secundaria da matriz
                } else if ((matrizJogo[0][2] == matrizJogo[1][1]) && (matrizJogo[1][1] == matrizJogo[2][0])) {
                    btnc.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btng.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("11" + matrizJogo[1][1] + "1"));
                    chamaTelaVencedor();

                    //Testa a segunda coluna da matriz
                } else if ((matrizJogo[0][1] == matrizJogo[1][1]) && (matrizJogo[1][1] == matrizJogo[2][1])) {
                    btnb.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btnh.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("11" + matrizJogo[1][1] + "1"));
                    chamaTelaVencedor();

                    //Testa a segunda linha sa matriz
                } else if ((matrizJogo[1][0] == matrizJogo[1][1]) && (matrizJogo[1][1] == matrizJogo[1][2])) {
                    btnd.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btnf.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("11" + matrizJogo[1][1] + "1"));
                    chamaTelaVencedor();
                }else{
                    sendMessage(String.valueOf("11" + matrizJogo[1][1]+"0"));
                }
            }
        }
    };
    View.OnClickListener buttonf = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(matrizJogo[1][2] == ' ') {
                if (jogador == 1) {
                    btnf.setText("X");
                    matrizJogo[1][2] = 'X';
                } else if (jogador == 2) {
                    btnf.setText("O");
                    matrizJogo[1][2] = 'O';
                }
                //Testa a terceira coluna da matriz
                if ((matrizJogo[0][2] == matrizJogo[1][2]) && (matrizJogo[1][2] == matrizJogo[2][2])) {
                    btnc.setBackgroundColor(173216230);
                    btnf.setBackgroundColor(173216230);
                    btni.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("12" + matrizJogo[1][2]+"1"));
                    chamaTelaVencedor();

                    //Testa a segunda linha da matriz
                } else  if ((matrizJogo[1][0] == matrizJogo[1][2]) && (matrizJogo[1][2] == matrizJogo[1][1])) {
                    btnd.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btnf.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("12" + matrizJogo[1][2] + "1"));
                    chamaTelaVencedor();
                }else{
                    sendMessage(String.valueOf("12" + matrizJogo[1][2]+"0"));
                }
            }
        }
    };
    View.OnClickListener buttong = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(matrizJogo[2][0] == ' ') {
                if (jogador == 1) {
                    btng.setText("X");
                    matrizJogo[2][0] = 'X';
                } else if (jogador == 2) {
                    btng.setText("O");
                    matrizJogo[2][0] = 'O';
                }
                //Testa a terceira linha da matriz
                if ((matrizJogo[2][1] == matrizJogo[2][0]) && (matrizJogo[2][0] == matrizJogo[2][2])) {
                    btng.setBackgroundColor(173216230);
                    btnh.setBackgroundColor(173216230);
                    btni.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("20" + matrizJogo[2][0]+"1"));
                    chamaTelaVencedor();

                    //Testa a primeira coluna da matriz
                } else if ((matrizJogo[0][0] == matrizJogo[2][0]) && (matrizJogo[2][0] == matrizJogo[1][0])) {
                    btna.setBackgroundColor(173216230);
                    btnd.setBackgroundColor(173216230);
                    btng.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("20" + matrizJogo[2][0] + "1"));
                    chamaTelaVencedor();

                    //Testa a diagonal secundaria da matriz
                } else if ((matrizJogo[1][1] == matrizJogo[2][0]) && (matrizJogo[2][0] == matrizJogo[0][2])) {
                    btng.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btnc.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("20" + matrizJogo[2][0] + "1"));
                    chamaTelaVencedor();
                }else{
                    sendMessage(String.valueOf("20" + matrizJogo[2][0]+"0"));
                }
            }
        }
    };
    View.OnClickListener buttonh = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(matrizJogo[2][1] == ' ') {
                if (jogador == 1) {
                    btnh.setText("X");
                    matrizJogo[2][1] = 'X';
                } else if (jogador == 2) {
                    btnh.setText("O");
                    matrizJogo[2][1] = 'O';
                }
                //Testa a terceira linha da matriz
                if ((matrizJogo[2][1] == matrizJogo[2][0]) && (matrizJogo[2][0] == matrizJogo[2][2])) {
                    btng.setBackgroundColor(173216230);
                    btnh.setBackgroundColor(173216230);
                    btni.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("21" + matrizJogo[2][1]+"1"));
                    chamaTelaVencedor();

                    //Testa a segunda coluna da matriz
                } else if ((matrizJogo[0][1] == matrizJogo[2][1]) && (matrizJogo[2][1] == matrizJogo[1][1])) {
                    btnb.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btnh.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("21" + matrizJogo[2][1] + "1"));
                    chamaTelaVencedor();
                }else{
                    sendMessage(String.valueOf("21" + matrizJogo[2][1]+"0"));
                }
            }
        }
    };
    View.OnClickListener buttoni = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(matrizJogo[2][2] == ' ') {
                if (jogador == 1) {
                    btni.setText("X");
                    matrizJogo[2][2] = 'X';
                } else if (jogador == 2) {
                    btni.setText("O");
                    matrizJogo[2][2] = 'O';
                }
                //Testa a terceira linha da matriz
                if ((matrizJogo[2][1] == matrizJogo[2][0]) && (matrizJogo[2][0] == matrizJogo[2][2])) {
                    btng.setBackgroundColor(173216230);
                    btnh.setBackgroundColor(173216230);
                    btni.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("22" + matrizJogo[2][2]+"1"));
                    chamaTelaVencedor();

                    //Testa a diagonal principal da matriz
                } else if ((matrizJogo[0][0] == matrizJogo[2][2]) && (matrizJogo[2][2] == matrizJogo[1][1])) {
                    btna.setBackgroundColor(173216230);
                    btne.setBackgroundColor(173216230);
                    btni.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("22" + matrizJogo[2][2]+"1"));
                    chamaTelaVencedor();

                    //Testa a terceira coluna da matriz
                } else if ((matrizJogo[0][2] == matrizJogo[2][2]) && (matrizJogo[2][2] == matrizJogo[1][2])) {
                    btnc.setBackgroundColor(173216230);
                    btnf.setBackgroundColor(173216230);
                    btni.setBackgroundColor(173216230);
                    sendMessage(String.valueOf("22" + matrizJogo[2][2]+"1"));
                    chamaTelaVencedor();
                }else{
                    sendMessage(String.valueOf("22"+matrizJogo[2][2]+"0"));
                }
            }
        }
    };

    public void onStart() {
        super.onStart();

        // Verifica ao iniciar se o BT já está ativado
        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT); // Requisita habilitação do bluetooth

            // Chama método para preparar futuras conexões
        } else {
            if (mBtService == null) setupConnection();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
		/*
		 * Caso o BT não tenha sido ligado no inicio,
		 * deve ser habilitado nesse momento.
		 * onResume() é executada após o retorno da
		 * requisição de ativação do adaptador
		 */
        if (mBtService != null) {
            // Estado NONE idica que as threads de conexão ainda não foram iniciadas
            if (mBtService.getState() == BluetoothService.STATE_NONE) {
                // Inicializa as threads necessárias para comunicação -> Ver BluetoothService.java
                mBtService.start();
            }
        }
    }

    private void setupConnection() {

        // Pega a referencia para os componentes de layout
        //msgTextView = (TextView) findViewById(R.id.textView_msg);
        //sendButton = (Button) findViewById(R.id.button_send);

        // Listener do botão enviar
        /*sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mBtService != null) {
                    sendMessage(":D");
                }
            }
        });*/
        // Inicialização dos serviços relacionados à comunicação bluetooth
        mBtService = new BluetoothService(this, mHandler);
    }

/*
    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    */

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mBtService != null){
            mBtService.stop();
        }
    }



    // Função para enviar uma string por bluetooth
    private void sendMessage(String message) {

        desabilitaBotoes();

        if(jogador == 1){
            jogador = 2;
        }else{
            jogador = 1;
        }

        // Verifica se há conexão
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "Não há conexão estabelecida!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se há informação para ser enviada
        if (message.length() > 0) {
            // Pega a string passada como parâmetro e transforma em um vetor de bytes
            byte[] send = message.getBytes();
            // Passa o vetor de bytes para o método de envio da classe bluetooth
            mBtService.write(send);
            // Limpa a mensagem (para evitar envios errados)
            message="";
        }

        //Variavel que guarda o numero de jogadas(no envio e na recepcao)
        //se chegar ao numero de 10 jogadas(9 da matriz + 1 do envio do nome)
        //entao e pq a matriz esta cheia e ninguem ganhou.
        matrizCheia++;
        if(matrizCheia == 10){
            flagGanhador = 2;
            chamaTelaVencedor();
        }
    }

    // Criação de um handler para poder se comunicar com a classe BluetoothService e tomar ações
    // na MainActivity  de acordo com o estado atual da conexão.
    // Aqui as mensagens recebidas e enviadas podem ser obtidas e exibidas na tela
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:      // Se houve troca no estado da conexão
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED: // Se o estado passou para conectado
                            Toast.makeText(getApplicationContext(),"Connected to "+ mConnectedDeviceName,
                                    Toast.LENGTH_LONG).show();

                            enviaNome();
                            break;
                        case BluetoothService.STATE_CONNECTING: // Se o estado passou para conectando
                            Toast.makeText(getApplicationContext(),"Connecting...",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;

                case MESSAGE_WRITE:     // Se está enviando uma mensagem
                    // Coloca que a mensagem foi enviada no textView
                    //msgTextView.setText("Enviei !");
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    break;

                case MESSAGE_READ:      // Se está recebendo uma mensagem
                    // Obtém a mensagem recebida
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    // Coloca a mensagem recebida no text view
                    //msgTextView.setText(readMessage);

                    habilitaBotoes();

                    //Atualiza matriz
                    atualizaMatriz(readMessage);
                    break;

                case MESSAGE_DEVICE_NAME:       // Se está recebendo o nome do dispositivo
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    // Método para obter as respostas das chamadas startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CONNECT_DEVICE:     // Chamado qndo o usuário clica em connect no menu
                // Quando a classe ListDevice retorna um dispositivo para se conectar
                if (resultCode == Activity.RESULT_OK) {
                    // Recebimento do MAC
                    String address = data.getExtras().getString(TelaDeviceList.EXTRA_DEVICE_ADDRESS);
                    // Recebe o objeto do dispositivo a se conectar
                    BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(address);
                    // Tenta estabelecer uma conexão
                    mBtService.connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:      // Retorno da requisição de habilitar bt

                if (resultCode == Activity.RESULT_OK) {
                    // BT ligado, prepara para conexões
                    setupConnection();
                } else {
                    // Caso usuário não habilite o bt, encerra o aplicativo
                    Toast.makeText(this, "Bluetooth não ativado...", Toast.LENGTH_SHORT).show();
                    finish();
                }

        }
    }
    // Define layout do menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tela_jogo, menu);
        return true;
    }

    // Opções do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                // Inicia ListDevices para procura e conexão de dispositivos
                Intent serverIntent = new Intent(this, TelaDeviceList.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                // Garante que o dispositivo está visível para outros
                if (myBluetoothAdapter.getScanMode() !=
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);
                }
                return true;
        }
        return false;
    }
}
