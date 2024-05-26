package personal.userIdentity.controller;

import static personal.userIdentity.util.constants.ADD_CONTACT;
import static personal.userIdentity.util.constants.DELETE_ALL;
import static personal.userIdentity.util.constants.IDENTITY;
import static personal.userIdentity.util.constants.STATUS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import personal.userIdentity.model.Contact;
import personal.userIdentity.service.IdentityService;
import personal.userIdentity.controller.response.ContactDetail;
import personal.userIdentity.controller.request.UserDetail;

@RestController

public class identityController {

  @Autowired
  IdentityService identityService;

  @PostMapping(IDENTITY)
  ContactDetail getContactDetail(@RequestBody UserDetail userDetail){
    return identityService.getContactDetail(userDetail);
  }

  @GetMapping(DELETE_ALL)
  void deleteAll(){
     identityService.deleteAll();
  }

  @GetMapping(STATUS)
  String status(){
    return identityService.status();
  }

}
