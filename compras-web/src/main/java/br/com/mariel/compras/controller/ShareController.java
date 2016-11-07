package br.com.mariel.compras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.repository.IUser;
import br.com.mariel.compras.service.IShareService;

@RestController
@RequestMapping("share")
public class ShareController {
	
	@Autowired private IShareService shareService;
	@Autowired private IUser userSql;
	
    @RequestMapping("/insert")
    public User shareBuy(@RequestBody Share share) {
    	if(share != null) {
    		User user = userSql.findById(share.getUser().getUser_phone());

    		if(user != null){
    			share.setUser(user);
    			if(shareService.share(share))
    				return user;
    		}
    	}
    	return null;
    }
    
    @RequestMapping("/delete")
    public Boolean shareDelete(@RequestBody Share share) {
    	if(share != null) 
    		return shareService.unshare(share);
    	
        return false;
    }
}