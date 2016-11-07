package br.com.compras.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.compras.app.service.ContactUtil;
import br.com.compras.app.service.ServiceHttpUtil;
import br.com.compras.app.service.SessionRepository;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.User;

public class ContactActivity extends Activity {

    private List<Buy> listBuy;
    private ContactUtil contactUtil;
    static final int INTENT_CONTACT  = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Grupo selecionado
        Intent intent = getIntent();
        if (intent != null) {
            Bundle param = intent.getExtras();
            if (param != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Buy>>(){}.getType();

                listBuy = gson.fromJson(param.getString("buys"), listType);

                contactUtil = new ContactUtil(getContentResolver());

                // Pega os contatos via Intent
                Intent it = contactUtil.getContatoFromIntent();
                startActivityForResult(it, INTENT_CONTACT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (INTENT_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    // Recupera o retorna da Intent
                    Uri contactData = data.getData();

                    // Cria um Cursor para o contato selecionado
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);

                    List<User> users = contactUtil.getContatosFromCursor(c);

                    if(users != null && users.size() > 0 && users.get(0).getPhones().size() > 0) {
                        Gson gson = new Gson();
                        String[] params = {gson.toJson(users.get(0))};

                        new Operation().execute(params);
                    }
                }
                break;
        }
    }

    class Operation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = getResources().getString(R.string.url_web_service)+"/user/exist";
            String phone = ServiceHttpUtil.post(url, params[0]);

            if(phone != null && phone != "") {
                Gson gson = new Gson();
                User user = gson.fromJson(params[0], User.class);
                user.setUser_phone(phone);

                /*
                Share share = Share.reload(buy, buy.getUser(), user);
                String url = getResources().getString(R.string.url_web_service)+"/share/insert";

                String returno = ServiceHttpUtil.post(url, gson.toJson(share));

                user = gson.fromJson(returno, User.class);

                if(user != null) {
                    if(userSql.insert(user) != null && shareSql.insert(share))
                        return "Sucess";
                }
                return "Error";

                user = userSql.insert(user);
                new SessionRepository(user);
                */
                return "Sucess";
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String result) {
            if("Sucess".equals(result)) {
                startActivity(new Intent(getBaseContext(), BuyGridActivity.class));
                finish();
            }else{
                Toast toast = Toast.makeText(getBaseContext(), R.string.erro_share_buys, Toast.LENGTH_SHORT);
                toast.show();

                startActivity(new Intent(getBaseContext(), BuyGridActivity.class));
                finish();
            }
        }
    }
}
