package br.com.mariel.compras.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.mariel.compras.domain.SyncList;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.service.ISyncronizerService;

import com.google.gson.Gson;

@RestController
@RequestMapping("sync")
public class SynchronizeController {
	
	@Autowired private ISyncronizerService service;
	
	@ResponseBody
    @RequestMapping("/put")
    public List<String> listToPut(@RequestBody String json) {
		Gson gson = new Gson();
		SyncList syncList = gson.fromJson(json, SyncList.class);
		List<String> list = service.listToRegister(syncList);
		
        return list;
    }
	
    @RequestMapping("/get")
    public String listToGet(@RequestBody String json) {
    	if(json != null && !json.isEmpty()) {
    		try {
	    		Gson gson = new Gson();
	    		User user = gson.fromJson(json, User.class);
				SyncList list = service.listProductToSync(user);
				
				return gson.toJson(list);
    		}catch (Exception erro){
				System.out.println(erro.getMessage());
			}
    	}
    	return null;
    }
	
	@RequestMapping("/del")
    public void syncOk(@RequestBody String json) {
		// Deleta da lista de sincronizacao
		Gson gson = new Gson();
		ArrayList<String> fromJson = gson.fromJson(json, ArrayList.class);
		
		if(!fromJson.isEmpty()) {
			for(String id : fromJson){
				service.delete(id);
			}
		}
    }
}