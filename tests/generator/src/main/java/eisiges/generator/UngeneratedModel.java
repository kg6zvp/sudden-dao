package eisiges.generator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author kg6zvp
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UngeneratedModel {
	@Id
	@GeneratedValue
	@Getter @Setter Long id;

	@Getter @Setter String property;
}
