package prototype.hibernate.search;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository  extends JpaRepository<Message, String> {
}
