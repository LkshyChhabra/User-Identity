package personal.userIdentity.controller.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
@Builder
public class ContactDetail {

  private Contacts contact;

  @Getter
  @Setter
  @Builder
  public static class Contacts {

    private int primaryContatctId;
    private List<String> emails;
    private List<String> phoneNumbers;
    private List<Integer> secondaryContactIds;
  }

}
