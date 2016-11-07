package br.com.compras.app;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import br.com.compras.app.repositoryImp.BuySql;
import br.com.compras.app.repositoryImp.ShareSql;
import br.com.compras.app.repositoryImp.UserSql;
import br.com.compras.app.service.ServiceHttpUtil;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IShare;
import br.com.mariel.compras.repository.IUser;

public class ShareActivity extends Activity {
	
	private EditText sharePhoneText;
	private Buy buyClicked;
	private IBuy buySql;
	private IUser userSql;
	private IShare shareSql;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_cad);
		
		buySql = new BuySql(this);
		userSql = new UserSql(this);
		shareSql = new ShareSql(this);
		
		Intent intent = getIntent();
		
		if(intent != null) {
			Bundle param = intent.getExtras();
			if(param != null) {
				buyClicked = Buy.reload(param.getLong("buy_id"), User.reload(param.getString("buy_phone")));
			}
		}
		
		sharePhoneText = (EditText) findViewById(R.id.sharePhoneText);
	}
	
	public void addShare(View view) {
		new Operation().execute("");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	class Operation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	Gson gson = new Gson();
        	User user = User.reload(sharePhoneText.getText().toString());
        	
        	Buy buy = buySql.findById(buyClicked);
        	
        	Share share = Share.reload(buy, buy.getUser(), user);
        	String url = getResources().getString(R.string.url_web_service)+"/share/insert";
        	
    		String returno = ServiceHttpUtil.post(url, gson.toJson(share));
			
    		user = gson.fromJson(returno, User.class);
    		
			if(user != null) {
				if(userSql.insert(user) != null && shareSql.insert(share))
					return "Sucess";
			}
            return "Error";
        }

        @Override
        protected void onPostExecute(String result) {
    		Intent intent = new Intent(getBaseContext(), ShareListActivity.class);
    			intent.putExtra("buy_id", buyClicked.getId());
				intent.putExtra("buy_phone", buyClicked.getUser().getUser_phone());
			
			startActivity(intent);
			finish();
        }
    }
}