package br.com.compras.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.com.compras.app.repositoryImp.UserSql;
import br.com.compras.app.service.ServiceHttpUtil;
import br.com.compras.app.service.SessionRepository;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.repository.IUser;
import br.com.mariel.compras.util.EmailValidator;

import com.google.gson.Gson;

public class LoginActivity extends Activity {

	private IUser userSql;
	private EditText name;
	private EditText mail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_cad);

		userSql = new UserSql(this);

		name = (EditText) findViewById(R.id.userNameText);
		mail = (EditText) findViewById(R.id.userEmailText);
	}

	// Valida o nome informado
	private boolean validName(){
		if(!name.getText().toString().isEmpty()){
			return true;
		}else{
			Toast toast = Toast.makeText(getBaseContext(), R.string.erro_valid_name, Toast.LENGTH_SHORT);
			toast.show();
		}
		return false;
	}

	// Valida o email informado
	private boolean validMail(){
		EmailValidator email = new EmailValidator();

		if(email.validate(mail.getText().toString())){
			return true;
		}else{
			Toast toast = Toast.makeText(getBaseContext(), R.string.erro_valid_mail, Toast.LENGTH_SHORT);
			toast.show();
		}
		return false;
	}

	public void saveUser(View view) {
		if(validName() && validMail()){
			TelephonyManager tMgr = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);

			String[] params= {name.getText().toString(), mail.getText().toString(), tMgr.getLine1Number()};
			new Operation().execute(params);
		}
	}

	class Operation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	Gson gson = new Gson();
        	String url = getResources().getString(R.string.url_web_service)+"/user/put";

    		User user = User.reload(params[0], params[1], params[2]);
    		String json = gson.toJson(user);

    		String userJson = ServiceHttpUtil.post(url, json);

    		if(Boolean.parseBoolean(userJson)) {
    			user = userSql.insert(user);
    			new SessionRepository(user);

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
				Toast toast = Toast.makeText(getBaseContext(), R.string.erro_register_login, Toast.LENGTH_SHORT);
				toast.show();
			}
        }
    }
}
