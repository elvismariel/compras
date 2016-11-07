package br.com.mariel.compras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import br.com.mariel.compras.domain.Phone;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.repository.IUser;
import br.com.mariel.compras.service.IShareService;

@RestController
@RequestMapping("user")
public class UserController {
	
	@Autowired private IUser userSql;
	@Autowired private IShareService shareService;
	
	@RequestMapping("/put")
    public Boolean insert(@RequestBody String json) {
    	if(json != null && !json.isEmpty()) {
			try {
				Gson gson = new Gson();
				User user = gson.fromJson(json, User.class);

				if (userSql.findById(user.getUser_phone()) != null) {
					return shareService.restoreUserDada(user);
				} else {
					return userSql.insert(user) != null ? true : false;
				}
			}catch (Exception erro){
				return false;
			}
    	}
        return false;
    }
	
	@RequestMapping("/exist")
    public String userExist(@RequestBody String json) {
    	if(json != null && !json.isEmpty()) {
			try {
				Gson gson = new Gson();
				User user = gson.fromJson(json, User.class);
				
				for(Phone phone : user.getPhones()){
					if(user.getPhones() == null)
						continue;
					
					String nunber = phone.getPhone().replaceAll("[^0123456789]", "");
					
					if (userSql.findById(nunber) != null){
						user.setUser_phone(nunber);
						break;
					}
				}
				
				if(user.getUser_phone() != null && !user.getUser_phone().isEmpty())
					return user.getUser_phone();
			}
			catch (Exception erro){
				System.out.print(erro.getMessage());
			}
    	}
        return "";
    }
}