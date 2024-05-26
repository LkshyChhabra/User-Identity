package personal.userIdentity.repository;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import personal.userIdentity.model.Contact;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
//  @Query("SELECT c FROM Contact c  WHERE c.email = :email limit 1")
  Optional<Contact> findFirstByEmail(String email);
//  @Query("SELECT c FROM Contact c  WHERE c.phoneNumber = :phoneNumber limit 1")
  Optional<Contact> findFirstByPhoneNumber(String phoneNumber);

  @Modifying
  @Transactional
  @Query("UPDATE Contact c SET c.linkedId = :newId WHERE c.linkedId = :oldId")
  int updateLinkedId(int oldId, int newId);

  @Modifying
  @Transactional
  @Query("UPDATE Contact c SET c.linkedId = :linkedId, c.linkPrecedence = 'secondary' WHERE c.id = :id")
  int updateContactById(int id, int linkedId);

  @Query("SELECT c FROM Contact c WHERE c.linkedId = :id and c.id != :id")
  List<Contact> getRelatedContacts(int id);


  @Query("SELECT count(distinct(c.linkedId)) FROM Contact c")
  int getDistinctUsers();
}

