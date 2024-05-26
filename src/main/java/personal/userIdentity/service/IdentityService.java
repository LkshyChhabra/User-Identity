package personal.userIdentity.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import personal.userIdentity.controller.request.UserDetail;
import personal.userIdentity.controller.response.ContactDetail;
import personal.userIdentity.controller.response.ContactDetail.Contacts;
import personal.userIdentity.model.Contact;
import personal.userIdentity.model.enums.LinkPrecedence;
import personal.userIdentity.repository.ContactRepository;

import static java.util.Objects.isNull;

@Service
public class IdentityService {

  @Autowired
  private ContactRepository contactRepository;

  private int saveContact(UserDetail userDetail, LinkPrecedence linkPrecedence, Integer linkedId) {
    Contact savedContact = null;
    Contact newContact = Contact.builder().email(userDetail.getEmail())
        .phoneNumber(userDetail.getPhoneNumber())
        .createdAt(
            LocalDateTime.now()).updatedAt(LocalDateTime.now()).linkedId(linkedId)
        .linkPrecedence(linkPrecedence.name()).build();
    savedContact = contactRepository.save(newContact);

    if (isNull(linkedId)) {
      savedContact.setLinkedId(savedContact.getId());
      contactRepository.save(newContact);
    }
    return savedContact.getLinkedId();
  }

  public ContactDetail getContactDetail(UserDetail userDetail) {

    Contact existingContactwithEmail = null, existingContactwithPhoneNumber = null;
    Contact savedContact = null;
    int primaryId = 0;
    if (!isNull(userDetail.getEmail())) {
      existingContactwithEmail = contactRepository.findFirstByEmail(userDetail.getEmail())
          .orElse(null);
    }
    if (!isNull(userDetail.getPhoneNumber())) {
      existingContactwithPhoneNumber = contactRepository.findFirstByPhoneNumber(
              userDetail.getPhoneNumber())
          .orElse(null);
    }

    if (isNull(existingContactwithPhoneNumber) && isNull(existingContactwithEmail)) {
      primaryId = saveContact(userDetail, LinkPrecedence.primary, null);
    } else if (isNull(existingContactwithPhoneNumber)) {
      primaryId = saveContact(userDetail, LinkPrecedence.secondary,
          existingContactwithEmail.getLinkedId());
    } else if (isNull(existingContactwithEmail)) {
      primaryId = saveContact(userDetail, LinkPrecedence.secondary,
          existingContactwithPhoneNumber.getLinkedId());
    } else {
      if (!existingContactwithPhoneNumber.getLinkedId()
          .equals(existingContactwithEmail.getLinkedId())) {
        Contact ParentConstactforPhone = null, ParentConstactforEmail = null;
        ParentConstactforPhone = contactRepository.getById(
            existingContactwithPhoneNumber.getLinkedId());
        ParentConstactforEmail = contactRepository.getById(existingContactwithEmail.getLinkedId());
        if (ParentConstactforPhone.getCreatedAt().isBefore(ParentConstactforEmail.getCreatedAt())) {
          primaryId = saveContact(userDetail, LinkPrecedence.secondary,
              ParentConstactforPhone.getId());
          contactRepository.updateLinkedId(ParentConstactforEmail.getId(),
              ParentConstactforPhone.getId());
          contactRepository.updateContactById(ParentConstactforEmail.getId(),
              ParentConstactforPhone.getId());
        } else {
          primaryId = saveContact(userDetail, LinkPrecedence.secondary,
              ParentConstactforEmail.getId());
          contactRepository.updateLinkedId(ParentConstactforPhone.getId(),
              ParentConstactforEmail.getId());
          contactRepository.updateContactById(ParentConstactforPhone.getId(),
              ParentConstactforEmail.getId());
        }
      } else {
        primaryId = existingContactwithPhoneNumber.getLinkedId();
      }
    }

    List<Contact> allRelatedContacts = contactRepository.getRelatedContacts(primaryId);
    Contact primaryContact = contactRepository.getById(primaryId);
    List<String> emails = allRelatedContacts.stream()
        .map(Contact::getEmail)
        .distinct()
        .collect(Collectors.toList());

    if (!emails.contains(primaryContact.getEmail())) {
      emails.add(primaryContact.getEmail());
    }

    List<String> phoneNumbers = allRelatedContacts.stream()
        .map(Contact::getPhoneNumber)
        .distinct()
        .collect(Collectors.toList());

    if (!phoneNumbers.contains(primaryContact.getPhoneNumber())) {
      phoneNumbers.add(primaryContact.getPhoneNumber());
    }

    List<Integer> secondaryContactIds = allRelatedContacts.stream()
        .map(Contact::getId)
        .distinct()
        .collect(Collectors.toList());

    return ContactDetail.builder().contact(
        Contacts.builder().primaryContatctId(primaryId).emails(emails).phoneNumbers(phoneNumbers)
            .secondaryContactIds(secondaryContactIds).build()).build();


  }

  public void deleteAll() {
    contactRepository.deleteAll();
  }

  public String status() {
    int totalUsers = contactRepository.getDistinctUsers();
    return "Currently there are " + totalUsers + " Users";
  }


}
